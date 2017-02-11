(ns investment-tracker.tax-lot
  (:require [clojure.spec :as s]))

(s/def ::quantity bigdec?)
(s/def ::transactions coll?)

(s/def ::tax-lot (s/keys :req [::quantity ::transactions]))
