(ns investment-tracker.fin-trans
  (:require [clojure.spec :as s]
            [investment-tracker.security :as security]))

(s/def ::id string?)                                        ;format ?
(s/def ::trade-date inst?)
(s/def ::settlement-date inst?)
(s/def ::action keyword?)                                   ;Finite set
(s/def ::quantity bigdec?)
(s/def ::price bigdec?)
(s/def ::tx-cost bigdec?)
(s/def ::split-ratio bigdec?)
(s/def ::comment string?)
(s/def ::security ::security/security)

(s/def ::tax-lot
  (s/keys
    :req [::id ::trade-date ::settlement-date ::action ::security ::quantity ::price]
    :opt [::tx-cost ::split-ratio ::comment]))

(defrecord FinTrans [id trade-date settlement-date action quantity price tx-cost split-ratio comment security])