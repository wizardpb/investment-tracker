(ns investment-tracker.security
  (:require [clojure.spec :as s]
            [investment-tracker.db :as db]))

(s/def ::name string?)
(s/def ::type keyword?)
(s/def ::ticker keyword?)
(s/def ::cusip string?)
(s/def ::isin string?)

(s/def ::security
  (s/keys
    :req [::name ::type ::ticker]
    :opt [::isin ::cusip]))

(defrecord Security [name type ticker cusip isin])

(defn db->Security [entity]
  (db/make-record map->Security entity))

(defn get-security [ident]
  (db->Security (db/get-security ident)))
