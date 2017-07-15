(ns investment-tracker.protocols
  (:import (org.h2.value ValueJavaObject$NotSerialized)))

(defprotocol Storeable
  "A protocol to interface a record to Datomic - generates a transaction structure for teh given keys and write to
  Datomic"
  (db-namespace [this] "Return the database namespace (as a String) for all my keys")
  (update-db [this] [this keys]
    "Return an map that saves the given keys of this record plus an attribute to add it to the container. If no keys
    are given, all non-nil keys are used. If a new entity is created, the new entity id is saved in the record as :id")
  )

(defprotocol FinTrans
  "A protocol to execute a financial transaction"
  (execute [this account] "Apply this trade to the given account"))

(defprotocol Tradeable
  "Some transaction that causes a trade"
  (buy [this accout])
  (sell [this accout]))

(defprotocol Valuable
  (value [this valueDate] "Provide the value of this element as of the value date"))
