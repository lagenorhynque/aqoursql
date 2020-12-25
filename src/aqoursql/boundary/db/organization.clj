(ns aqoursql.boundary.db.organization
  (:require
   [clojure.spec.alpha :as s]))

(s/def ::id nat-int?)
(s/def ::name string?)
