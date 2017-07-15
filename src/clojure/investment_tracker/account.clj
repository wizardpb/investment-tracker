(ns investment-tracker.account
  (:require [clojure.spec :as s]
            [datomic.api :as d]
            [investment-tracker.db :as db])
  (:use investment-tracker.protocols
        investment-tracker.position))

(defrecord Account [custodian-id name transactions positions]
  Storeable
  (db-namespace [this] "account")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this (keys this))))

(defn db->Account [entity]
  (-> (db/make-record map->Account entity)
    (update :positions (fnil #(map db->Position %) []))))


(defn add-position [this security]
  (let [posn (update-db (map->Position {:security security}))]
    (->> posn
     (assoc this :positions)
     (update-db))
    posn)
  )

(defn find-position [this security]
  (some #(if (= (get-in % [:security :ticker]) (:ticker security)) %) (:positions this)))

(defn find-or-add-position [this security]
  (if-let [posn (find-position this security)]
    posn
    (add-position this security)))

(defn add-transaction [this txn]
  (-> this
    (assoc :transactions txn)
    (update-db [:id :transactions])
    :id
    (db/get-entity)))

(defn get-account [custodian-id]
  (db->Account (db/get-account custodian-id)))

