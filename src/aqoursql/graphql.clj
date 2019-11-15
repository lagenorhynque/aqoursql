(ns aqoursql.graphql
  (:require [aqoursql.resolver.artists :as artists]
            [aqoursql.resolver.members :as members]
            [aqoursql.resolver.songs :as songs]
            [clojure.java.io :as io]
            [com.walmartlabs.lacinia.parser.schema :as parser.schema]
            [com.walmartlabs.lacinia.pedestal :as lacinia.pedestal]
            [com.walmartlabs.lacinia.schema :as schema]
            [integrant.core :as ig]))

(def attach-map
  {:resolvers {:Query {:artist_by_id artists/fetch-artist-by-id
                       :artists artists/list-artists
                       :member_by_id members/fetch-member-by-id
                       :members members/list-members
                       :song_by_id songs/fetch-song-by-id
                       :songs songs/list-songs}}})

(defmethod ig/init-key ::schema
  [_ {:keys [path]}]
  (-> (io/resource path)
      slurp
      (parser.schema/parse-schema attach-map)))

(defmethod ig/init-key ::service
  [_ {:keys [schema options]}]
  (lacinia.pedestal/service-map (schema/compile schema) options))
