(ns investment-tracker.position-test
  (:require [clojure.test :refer :all]
            [investment-tracker.position :refer :all])
  (:import (java.util Date)))

(deftest tax-lots
  (testing "Creation"
    (is (= (->TaxLot {:lot/action :Buy
                      :lot/quantity 100M
                     :lot/price 10M
                     :lot/tx-cost 7.95M
                     :lot/description "Buy 100 AAPL @ $10"
                     :lot/trade-date (Date. 116 3 15)
                     :lot/settlement-date (Date. 116 3 18)})
           {:domain/datatype :TaxLot
            :lot/action :Buy
            :lot/quantity 100M
            :lot/price 10M
            :lot/tx-cost 7.95M
            :lot/description "Buy 100 AAPL @ $10"
            :lot/trade-date (Date. 116 3 15)
            :lot/settlement-date (Date. 116 3 18)
            :lot/realized-gain 0M
            :lot/unrealized-gain 0M
            })))
  )

(deftest equity-position
  (let [instrument
        {:db/id 10000
         :domain/datatype :Equity
         :instrument/name "Apple Inc."
         :instrument/type :instrument.type/equity
         :equity/ticker :AAPL}
        ]
    (testing "Creation"
      (is (= (->EquityPosition instrument )
             {:domain/datatype        :EquityPosition
              :position/instrument    instrument
              :position/lots          []
              }
             )))))
