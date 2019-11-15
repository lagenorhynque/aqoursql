(ns aqoursql.resolver.members
  (:require [aqoursql.boundary.db.member :as db.member]
            [aqoursql.util.validator :refer [when-valid]]
            [struct.core :as st]))

(defn fetch-member-by-id [{:keys [db]} {:keys [id]} _]
  (db.member/find-member-by-id db id))

(defn list-members [{:keys [db]} args _]
  (when-valid [[] args [[:name [st/min-count 1]]
                        [:organization_name [st/min-count 1]]]]
    (db.member/find-members db args)))
