(ns investment-tracker.account-tests
  (:require [clojure.test :refer :all]
            [investment-tracker.test-helpers :refer :all]
            [investment-tracker.protocols :refer :all]
            [investment-tracker.security :refer :all]
            [investment-tracker.account :refer :all]
            [investment-tracker.position :refer :all]
            [investment-tracker.fin-trans :refer :all]
            )
  (:import (investment_tracker.position Position)))

(deftest FindOrAdd
  (with-test-db
    (testing "Create new position"
      (is (zero? (count (:positions (get-test-account)))))
      (let [posn (find-or-add-position (get-test-account) (get-security :AAPL))]
        (is (instance? Position posn))
        (is (= (:id (:security posn)) (:id (get-security :AAPL))))
        (is (= 1 (count (:positions (get-test-account)))))))
    (testing "Find existing position"
      (is (= 1 (count (:positions (get-test-account)))))
      (let [posn (first (:positions (get-test-account)))
            found-posn (find-or-add-position (get-test-account) (get-security :AAPL))]
        (is (= 1 (count (:positions (get-test-account)))))
        (is (= (:id posn) (:id found-posn)))))))
