(ns investment-tracker.security
  (:require [clojure.spec :as s]
            [investment-tracker.db :as db]))

(defrecord Security [name type ticker cusip isin])

(defn db->Security [entity]
  (db/make-record map->Security entity))

(defn get-security [ticker]
  (db->Security (db/get-entity [:security/ticker ticker])))

