(ns investment-tracker.account
  (:require [clojure.spec :as s]
            [investment-tracker.db :as db]))

(defrecord Account [custodian-id name transactions positions])

(defn db->Account [entity]
  (db/make-record map->Account entity))

(defn get-account [custodian-id]
  (db->Account (db/get-account custodian-id)))


