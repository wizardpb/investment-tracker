(ns investment-tracker.position-test
  (:require [clojure.test :refer :all]
            [investment-tracker.position :refer :all])
  (:import (java.util Date)))

(deftest tax-lots
  (testing "Creation"
    (is (= (->TaxLot 100M [{:domain/datatype :FinTrans}])
           {:domain/datatype :TaxLot
            :tax-lot/quantity 100M
            :tax-lot/transactions [{:domain/datatype :FinTrans}]
            :tax-lot/realized-gain 0M,
            :tax-lot/unrealized-gain 0M
            })))
  )

(deftest equity-position
  (let [security
        {:db/id 10000
         :domain/datatype :Equity
         :security/name "Apple Inc."
         :security/type :instrument.type/stock
         :equity/ticker :AAPL}
        ]
    (testing "Creation"
      (is (= (->EquityPosition security [] )
             {:domain/datatype        :EquityPosition
              :position/security    security
              :position/lots          []
              }
             )))))
