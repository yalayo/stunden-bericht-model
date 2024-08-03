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
	 :headers {"Content-Type" "text/html"}
	 :body (-> body
            (h/html)
            (str))})

(defn template [html-body]
	[:html
 	 [:head
      [:title "Title"]
      (hp/include-js "https://cdn.tailwindcss.com" "https://unpkg.com/htmx.org@1.9.4?plugins=forms")]
     [:body (h/raw html-body)]])

(defn htmx-test []
  [:div {:class "container mx-auto mt-10"} "Prueba"])

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
						 ::http/port   (Integer. (or (env :port) 5000))}) ; Service map

(defn -main [& args] (-> service-map http/create-server http/start)) ; Server Instance