(ns app.main
	(:require [clojure.spec.alpha :as s])
	(:gen-class))

;; Specs for the modeled data so far.
(s/def ::project-id string?)
(s/def ::employee-id string?)
(s/def ::hours-per-day int?)
(s/def ::hourly-rate double?)
(s/def ::date inst?)

(s/def ::settings (s/keys :req-un [::project-id  ::employee-id ::hours-per-day ::hourly-rate]))
(s/def ::projects-settings (s/coll-of ::settings))
(s/def ::work-entry (s/keys :req-un [::project-id  ::employee-id ::worked-hours]))

(s/def ::day (s/keys :req-un [::date]))
(s/def ::days (s/coll-of ::day))
(s/def ::plan (s/keys :req-un [::employee-id ::days]))
(s/def ::vacation-plans (s/coll-of ::plan))

(s/def ::holidays (s/coll-of ::day))

(def projects-settings [{:project-id "1" :employee-id "1" :hours-per-day 2 :hourly-rate 65.5}
	{:project-id "2" :employee-id "1" :hours-per-day 6 :hourly-rate 85.5}])

(def vacation-plans [{:employee-id "1" :days [{:date "10.01.2023"}]}
{:employee-id "2" :days [{:date "10.01.2023"}]}])

(def holidays [{:date "10.01.2023"}])

(defn -main [& args]
	(println "Hello world!"))