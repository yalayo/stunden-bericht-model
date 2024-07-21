(ns app.main
	(:require [clojure.spec.alpha :as s])
	(:import [java.time LocalDate DayOfWeek YearMonth])
	(:import [java.time.format DateTimeFormatter])
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

;; Amount of hours not worked on a specific day
(def partial-days [{:date "16.01.2023" :hours 3} {:date "17.01.2023" :hours 1}])

;; Possible high level functions

;; Helper functions
;; How to get the first and the last day of a given month
(defn last-day-of-month [year month]
	(let [year-month (YearMonth/of year month)
				end-of-month (.atEndOfMonth year-month)
				formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy")]
			(.format end-of-month formatter)))

;; How to get the working days of the month
;; Check if a given date is a weekend
(defn weekend? [date]
	(let [day-of-week (.getDayOfWeek date)]
		(or (= day-of-week DayOfWeek/SATURDAY)
				(= day-of-week DayOfWeek/SUNDAY))))

;; Check if a given date is a holiday
(defn holiday? [date holidays]
	(let [formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy")]
		(some #(= date (LocalDate/parse (:date %) formatter)) holidays)))

;; Get the working days of the month
#_(defn working-days-of-month [year month holidays]
	(let [year-month (YearMonth/of year month)
				days-in-month (.lengthOfMonth year-month)]
		(filter #(and (not (weekend? %))
									(not (holiday? % holidays)))
						(map #(LocalDate/of year month %)
								 (range 1 (inc days-in-month))))))

;; Formated date. User later, for the moment I only need the amout of worked days
(defn working-days-of-month [year month holidays]
	(let [year-month (YearMonth/of year month)
				days-in-month (.lengthOfMonth year-month)
				formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy")]
		(map #(.format % formatter)
				 (filter #(and (not (weekend? %))
											 (not (holiday? % holidays)))
								 (map #(LocalDate/of year month %)
											(range 1 (inc days-in-month)))))))

;; Calculate the total amount of hours of vacation given a date range
(defn vacation-hours [from to holidays]
	(let [formatter (DateTimeFormatter/ofPattern "dd.MM.yyyy")
				start-date (LocalDate/parse from formatter)
				end-date (LocalDate/parse to formatter)
				days (take-while (fn [date] (not (.isAfter date end-date)))
												 (iterate #(.plusDays % 1) start-date))
				vacation-days (filter #(and (not (weekend? %))
																		(not (holiday? % holidays))) days)]
		(* 8 (count vacation-days))))

;; Calculate the total amount of not worked hours for other reasons other than vacations and holidays
(defn not-used-working-hours [partial-days]
	(reduce + (map :hours partial-days)))

;; Calculate the amount of hours worked in a given date range (by default the whole month)
;; Holidays, vacations and days not worked for special reasons should be subtracted
(defn worked-hours [year month holidays partial-days vacation-from vacation-to]
	(let [working-days (count (working-days-of-month year month holidays))
				potential-working-hours (* 8 working-days)
				total-vacation-hours (vacation-hours vacation-from vacation-to holidays)
				total-not-worked-hours (not-used-working-hours partial-days)]
		(- potential-working-hours total-vacation-hours total-not-worked-hours)))

(defn -main [& args]
	(let [year 2023
				month 1
				vacation-from "01.01.2023"
				vacation-to "08.01.2023"
				total-worked-hours (worked-hours year month holidays partial-days vacation-from vacation-to)]
		(println (str "Total worked hours (excluding vacations and partial days): " total-worked-hours))
		(println (working-days-of-month 2023 1 holidays))))