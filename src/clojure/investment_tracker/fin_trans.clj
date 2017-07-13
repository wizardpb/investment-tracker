(ns investment-tracker.fin-trans
  (:require [clojure.spec :as s]
            [investment-tracker.security :as security])
  (:use investment-tracker.protocols))

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


(defrecord EquityTrade [id trade-date settlement-date action quantity price tx-cost comment security]
  )