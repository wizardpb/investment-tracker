(ns investment-tracker.fin-trans
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols
        investment-tracker.account
        investment-tracker.security))


(defrecord EquityTrade [custodian-id trade-date settlement-date action quantity price tx-cost comment security]
  Storeable
  (db-namespace [this] "fin-trans")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this (keys this)))
  FinTrans
  (apply-to [this account]
    (case action
      :sell (sell this account)
      :buy (buy this account)))
  )

(defn db->EquityTrade [entity]
  (-> (db/make-record map->EquityTrade entity)
    (update :security #(db->Security (db/get-entity (:db/id %))))))