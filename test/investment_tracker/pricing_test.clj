(ns investment-tracker.pricing-test
  (:require [clojure.test :refer :all]
            [investment-tracker.pricing :refer :all]
            [investment-tracker.instrument :refer :all])
  (:import (java.util Date))
  )

(deftest yahoo-api
  (testing "URL formatting"
    (let [date (Date. 116 0 31)
          equities [:YHOO :AAPL]]
      (is (=
            (format-yahoo-url equities date)
            (str "http://query.yahooapis.com/v1/public/yql?q="
                 (str "select%20*%20from%20yahoo.finance.historicaldata%20"
                      "where%20symbol%20in%20(\"YHOO\",\"AAPL\")%20"
                      "and%20startDate%20=%20\"2016-01-31\"%20"
                      "and%20endDate%20=%20\"2016-01-31\"")
                 "&env=store://datatables.org/alltableswithkeys"
                 "&format=json")))))
  (testing "JSON parsing"
    (is (=
          (parse-yahoo-json [{:Symbol    "YHOO",
                                :Date      "2016-04-13",
                                :Open      "36.939999",
                                :High      "37.349998",
                                :Low       "36.900002",
                                :Close     "37.310001",
                                :Volume    "17158700",
                                :Adj_Close "37.310001"}
                               {:Symbol    "AAPL",
                                :Date      "2016-04-13",
                                :Open      "110.800003",
                                :High      "112.339996",
                                :Low       "110.800003",
                                :Close     "112.040001",
                                :Volume    "32691800",
                                :Adj_Close "112.040001"}])
          {:YHOO {
                  :Open  36.939999M,
                  :High  37.349998M,
                  :Low   36.900002M,
                  :Close 37.310001M
                  }
           :AAPL {
                  :Open  110.800003M,
                  :High  112.339996M,
                  :Low   110.800003M,
                  :Close 112.040001M,
                  }})
        ))
  (testing "Env creation"
    (let [env (->FinEnvFromYahoo
                (Date. 116 3 15)
                [:YHOO :AAPL])]
      (is (= (:tradeDate env) (Date. 116 3 15)))
      (is (= (closingPrice env :YHOO) 36.509998M))
      (is (= (closeData env :YHOO) {:Close 36.509998M
                                    :High  37.150002M
                                    :Low   36.419998M
                                    :Open  37.130001M})))
    )
  )