(ns investment-tracker.account
  (:require [clojure.spec :as s]
            [investment-tracker.db :as db])
  (:use investment-tracker.protocols
        investment-tracker.position)
  (:import (investment_tracker.position Position)))

(defrecord Account [custodian-id name transactions positions])

(defn db->Account [entity]
  (db/make-record map->Account entity))

(defn get-account [custodian-id]
  (db->Account (db/get-account custodian-id)))


