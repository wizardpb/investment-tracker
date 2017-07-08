(ns investment-tracker.position
  (:require [investment-tracker.db :as db])
  )

(defrecord Position [security lots])
