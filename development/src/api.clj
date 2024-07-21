(ns api (:require [io.pedestal.http :as http]
									[environ.core :refer [env]]
									[logic :refer [vacation-plans holidays]])
	(:gen-class))

; Route handler
(defn hello-world [request] {:status 200 :body "Hello"}) 

(defn get-vacations [_] {:status 200 :body vacation-plans})

(defn get-holidays [_] {:status 200 :body holidays})

;; Routes
(def routes #{["/" :get hello-world :route-name :hello-world]
	["/vacations" :get get-vacations :route-name :get-vacations]
	["/holidays" :get get-holidays :route-name :get-holidays]})


(def service-map {::http/routes routes
						 ::http/type   :immutant
						 ::http/host   "0.0.0.0"
						 ::http/join?  false
						 ::http/port   (Integer. (or (env :port) 5000))}) ; Service map

(defn -main [& args] (-> service-map http/create-server http/start)) ; Server Instance