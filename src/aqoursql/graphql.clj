(ns aqoursql.graphql
  (:require [aqoursql.resolver.artists :as artists]
            [aqoursql.resolver.members :as members]
            [aqoursql.resolver.songs :as songs]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.pedestal :as lacinia]
            [com.walmartlabs.lacinia.schema :as schema]
            [com.walmartlabs.lacinia.util :as util]
            [integrant.core :as ig]))

(def resolver-map
  {:query/artist-by-id artists/fetch-artist-by-id
   :query/artists artists/list-artists
   :query/member-by-id members/fetch-member-by-id
   :query/members members/list-members
   :query/song-by-id songs/fetch-song-by-id
   :query/songs songs/list-songs
   :Artist/members members/list-artist-members})

(defmethod ig/init-key ::schema
  [_ _]
  (-> (io/resource "aqoursql/graphql-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers resolver-map)
      schema/compile))

(defmethod ig/init-key ::service
  [_ {:keys [schema options]}]
  (lacinia/service-map schema options))
