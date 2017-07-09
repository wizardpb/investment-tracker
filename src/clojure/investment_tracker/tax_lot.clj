(ns investment-tracker.tax-lot
  (:require [clojure.spec :as s]
            [investment-tracker.db :as db]))

(s/def ::quantity bigdec?)
(s/def ::transactions coll?)

(s/def ::tax-lot (s/keys :req [::quantity ::transactions]))

(defrecord TaxLot [quantity txns]
  db/Updateable
  (update-map [this keys ref]
    (db/entity-map this keys ref))
  (update-map [this ref]
    (db/entity-map this [:quantity] ref))
  (update-map [this]
    (db/entity-map this [:quantity] nil))
  )
