(ns investment-tracker.util-tests
  (:require [clojure.test :refer :all]
            [investment-tracker.util :refer :all])
  (:import (java.util Date)))

(deftest util-test
  (testing "Format date"
    (is (= (format-date (Date. 116 0 31) "yyyy-MM-DD") "2016-01-31"))
    (is (= (format-date (Date. 116 0 31)) "2016-01-31")))
  (testing "Parse date"
    (is (= (parse-date "2016-01-31" "yyyy-MM-DD") (Date. 116 0 31)))
    (is (= (parse-date "2016-01-31") (Date. 116 0 31)))))

