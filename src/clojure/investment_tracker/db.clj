(ns investment-tracker.db
  "Functions to interface with the database"
  (:require [datomic.api :as d]
            [investment-tracker.system :as sys]))

(defn dbconn []
  (get-in sys/system [:db :conn]))

(defn getdb []
  (d/db (dbconn)))

(defn get-user [user-id]
  (d/entity (getdb) [:user/id user-id]))
