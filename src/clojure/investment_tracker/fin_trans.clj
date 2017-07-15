(ns investment-tracker.fin-trans
  (:require [investment-tracker.db :as db])
  (:use investment-tracker.protocols
        investment-tracker.account
        investment-tracker.position
        investment-tracker.tax-lot
        investment-tracker.security))

(declare buy-lot)

(defrecord EquityTrade [txn-id custodian-trade-id trade-date settlement-date action security quantity price tx-cost comment]
  Storeable
  (db-namespace [this] "fin-trans")
  (update-db [this keys] (db/update-record this keys))
  (update-db [this] (update-db this (keys this)))

  Tradeable
  (buy [this account]
    (add-transaction account this)
    (add-lot (find-or-add-position account (:security this)) (buy-lot this)))
  (sell [this account])

  FinTrans
  (execute [this account]
    (case action
      :sell (sell this account)
      :buy (buy this account)))
  )

(defn db->EquityTrade [entity]
  (-> (db/make-record map->EquityTrade entity)
    (update :security #(db->Security (db/get-entity (:db/id %))))))

(defn buy-lot [trade]
  (map->Tax-Lot
    {:quantity (:quantity trade)
     :price (:price trade)
     :create-date (:trade-date trade)
     :effective-date (:settlement-date trade)
     :transactions trade})
  )

