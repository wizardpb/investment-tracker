(ns investment-tracker.fin-trans-tests
  (:require [clojure.test :refer :all]
            [investment-tracker.test-helpers :refer :all]
            [investment-tracker.util :refer :all]
            [investment-tracker.protocols :refer :all]
            [investment-tracker.security :refer :all]
            [investment-tracker.account :refer :all]
            [investment-tracker.position :refer :all]
            [investment-tracker.fin-trans :refer :all]
            )
  (:import (java.util Date)))

(deftest Buy
  (with-test-db
    (testing "Storable"
      (is (:id (update-db (map->EquityTrade
                            {:custodian-trade-id "TestTrade"
                             :trade-date         (parse-date "2016-01-01") :settlement-date (parse-date "2016-01-04")
                             :action             :buy :quantity 1000M :price 10M :tx-cost 0M
                             :comment            ""
                             :security           (get-security :AAPL)})))))
    (testing "Buy equity"
      (let [trade (update-db (map->EquityTrade
                               {:custodian-trade-id "TestTrade"
                                :trade-date         (parse-date "2016-01-01") :settlement-date (parse-date "2016-01-04")
                                :action             :buy :quantity 1000M :price 10M :tx-cost 0M
                                :comment            ""
                                :security           (get-security :AAPL)}))
            posn (execute trade (get-account "142365483"))]
        (is (= (get-in posn [:security :id] ) (:id (get-security :AAPL))))
        (is (= (count (:lots posn)) 1))
        (is (= (value posn (parse-date "2016-01-04")) 10000M))
        (is (= (value posn (parse-date "2016-01-01")) 0M))
        ))))
