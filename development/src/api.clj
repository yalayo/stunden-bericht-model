(ns api (:require [io.pedestal.http :as http]
									[hiccup2.core :as h]
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
	 :body body})

(defn template [html-body]
	(str (h/html [:html [:body (h/raw html-body)]])))

(defn respond-hello [request]
	(let [name (get-in request [:query-params :name] "World")]
		(ok (template (str (h/html [:h1 {} "Hello, " name "!"]) )))))

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