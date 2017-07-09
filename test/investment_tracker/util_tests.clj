(ns investment-tracker.util-tests
  (:require [clojure.test :refer :all]
            [investment-tracker.util :refer :all])
  (:import (java.util Date)))

(deftest util-test
  (testing "Format date"
    (is (= (format-date (Date. 116 0 31) "YYYY-MM-dd")
           "2016-01-31"))))

