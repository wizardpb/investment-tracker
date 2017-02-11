(ns investment-tracker.security
  (:require [clojure.spec :as s]))

(s/def ::name string?)
(s/def ::type keyword?)
(s/def ::ticker keyword?)
(s/def ::cusip string?)
(s/def ::isin string?)

(s/def ::security
  (s/keys
    :req [::name ::type ::ticker]
    :opt [::isin ::cusip]))
