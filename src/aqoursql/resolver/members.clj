(ns aqoursql.resolver.members
  (:require
   [aqoursql.boundary.db.member :as db.member]
   [aqoursql.util.validator :as validator :refer [when-valid]]))

(defn fetch-member-by-id [{:keys [db]} {:keys [id]} _]
  (db.member/find-member-by-id db id))

(defn list-members [{:keys [db]} args _]
  (when-valid [[] args [:map
                        [:name {:optional true} validator/schema-maybe-non-empty-string]
                        [:organization_name {:optional true} validator/schema-maybe-non-empty-string]]]
    (db.member/find-members db args)))
