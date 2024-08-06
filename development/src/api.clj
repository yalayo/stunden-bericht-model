(ns api 
  (:require [io.pedestal.http :as http]
            [hiccup2.core :as h]
         	[hiccup.page :as hp]
            [environ.core :refer [env]] 
            [logic :refer [vacation-plans holidays]])
 (:gen-class))

; Route handler
(defn hello-world [request] {:status 200 :body "Hello"}) 

(defn get-vacations [_] {:status 200 :body vacation-plans})

(defn get-holidays [_] {:status 200 :body holidays})

(defn ok [body]
	{:status 200
	 :headers {"Content-Type" "text/html" "Content-Security-Policy" "img-src 'self'"}
	 :body (-> body
            (h/html)
            (str))})

(defn template [html-body]
	[:html
 	 [:head
      [:title "Title"]
 	  [:link {:href "https://unpkg.com/tailwindcss@^2/dist/tailwind.min.css" :rel "stylesheet"}]
 	  [:script {:src "htmx.min.js"}]]
     [:body (h/raw html-body)]])

(defn item-component [{:keys [id title description author]}]
  [:article.p-6.even:bg-white.odd:bg-slate-100.sm:p-8
   [:h2.break-all.text-lg.font-medium.sm:text-xl
    [:a.hover:underline
     {:href (str "htmx/infinite-scroll/item/" id)}
     title]]
   [:p.mt-1.break-all.text-sm.text-gray-700
    description]
   [:div.mt-4.text-xs.font-medium.text-gray-500
    [:div.flex.items-center.gap-2
     [:span.relative.flex.h-10.w-10.shrink-0.overflow-hidden.rounded-full
      [:img.aspect-square.h-full.w-full
       {:src (:picture author "https://avataaars.io/?hairColor=BrownDark")}]]
     [:span (:name author)]]]])

(defn htmx-test []
  [:div {:class "container mx-auto mt-10"}
   (item-component {:id "id" :title "Title" :description "Desc" :author {:name "Author"}})])

(defn respond-hello [request]
	(ok (template (str (h/html (htmx-test))))))

;; Routes
(def routes #{["/" :get hello-world :route-name :hello-world]
	["/vacations" :get get-vacations :route-name :get-vacations]
	["/holidays" :get get-holidays :route-name :get-holidays]
	["/greet" :get respond-hello :route-name :greet]})


(def service-map {::http/routes routes
						 ::http/type   :immutant
						 ::http/host   "0.0.0.0"
						 ::http/join?  false
             ::http/resource-path  "public"
						 ::http/port   (Integer. (or (env :port) 5000))}) ; Service map

(defn -main [& args] (-> service-map http/create-server http/start)) ; Server Instance