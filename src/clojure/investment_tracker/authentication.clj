(ns investment-tracker.authentication
  "All authentication and authorization functions"
  (:require [clojurewerkz.scrypt.core :as crypt]
            [investment-tracker.system :as sys]
            )
  (:import (javax.security.auth.login AccountNotFoundException)))

(defn validate-user [user username credential]
  (if user
    (and (crypt/verify credential (:investment-tracker.user/credentials user)) user)
    (throw (AccountNotFoundException. username))))

(defn login [user]
  (sys/system-assoc [:current-user] user))

(defn logout []
  (sys/system-dissoc [:current-user]))

(defn current-user []
  (get-in sys/system [:current-user]))

(defn current-user-id []
  (:user/id (current-user)))