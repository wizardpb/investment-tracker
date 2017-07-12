(ns investment-tracker.db-tests
  (:require [clojure.test :refer :all]
            [clojure.string :as str]
            [investment-tracker.system :as sys]
            [investment-tracker.db :as db]
            [investment-tracker.account :refer :all]
            [investment-tracker.position :refer :all]
            [investment-tracker.tax-lot :refer :all]
            [investment-tracker.fin-trans :refer :all]
            [investment-tracker.security :refer :all])
  (:import (java.util Date)))

(deftest Txn-test
  (testing "full"
    (is (= (db/->Txn (Date. 1000) :Util "user" "desc" [{:db/id 1 :attr "val"}])
          [{:db/id             "datomic.tx"
            :db/txInstant      (Date. 1000)
            :db.tx/type        :Util
            :db.tx/actor-id    "user"
            :db.tx/description "desc"}
           {:db/id 1 :attr "val"}])))
  (testing "no date"
    (is (= (db/->Txn :Util "user" "desc" [{:db/id 1 :attr "val"}])
          [{:db/id             "datomic.tx"
            :db.tx/type        :Util
            :db.tx/actor-id    "user"
            :db.tx/description "desc"}
           {:db/id 1 :attr "val"}])))
  (testing "desc and content"
    (sys/init)
    (alter-var-root #'sys/system assoc-in [:current-user :user/id] "User")
    (is (= (db/->Txn "desc" [{:db/id 1 :attr "val"}])
          [{:db/id             "datomic.tx"
            :db.tx/type        :UserTxn
            :db.tx/actor-id    "User"
            :db.tx/description "desc"}
           {:db/id 1 :attr "val"}])))
  (testing "content"
    (sys/init)
    (alter-var-root #'sys/system assoc-in [:current-user :user/id] "User")
    (is (= (db/->Txn [{:db/id 1 :attr "val"}])
          [{:db/id             "datomic.tx"
            :db.tx/type        :UserTxn
            :db.tx/actor-id    "User"
            :db.tx/description ""}
           {:db/id 1 :attr "val"}]))))

(deftest Transact-test
  (testing "temp Ids"
    (let [tempIds (db/transact [{:db/id "position.id"
                                 :position/security (:id (get-security :AAPL))}])]
      (is (= (set (keys tempIds)) #{"datomic.tx" "position.id"})))))

(deftest Entity-map-test
  (do
    (sys/init)
    (alter-var-root #'sys/system sys/open-db)
    (testing "DB Open"
      (is (and (db/get-connection) (db/getdb))))
    (testing "Non-ref keys"
      (let [rec (map->Tax-Lot {:quantity 100M})]
        (is (= (db/entity-map rec [:quantity])
              {:db/id            "tax-lot.id"
               :tax-lot/quantity 100M}))
        (is (= (db/entity-map rec [])
              {:db/id "tax-lot.id"}))
        (is (= (db/entity-map rec [:quantity :txns])
              {:db/id            "tax-lot.id"
               :tax-lot/quantity 100M}))
        )
      )
    (testing "Unsaved ref keys"
      (let [rec (map->Tax-Lot {:quantity 150M :transactions (map->Fin-Trans {:action "Buy"})})]
        (is (= (db/entity-map rec [:quantity :transactions])
              {:db/id    "tax-lot.id"
               :tax-lot/quantity 150M }))
        (is (= (db/entity-map rec [])
              {:db/id "tax-lot.id"}))
        )
      )
    (testing "Saved ref keys"
      (let [rec (map->Tax-Lot {:quantity 100M :transactions (assoc (map->Fin-Trans {:action "Buy"}) :id 10000)})]
        (is (= (db/entity-map rec [:quantity :transactions])
             {:db/id                "tax-lot.id"
              :tax-lot/quantity 100M
              :tax-lot/transactions 10000}))))
    ))
