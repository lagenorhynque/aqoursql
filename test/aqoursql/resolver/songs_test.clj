(ns aqoursql.resolver.songs-test
  (:require
   [aqoursql.test-helper.core :as helper :refer [with-db-data with-system]]
   [aqoursql.test-helper.db-data :as db-data]
   [aqoursql.util.const :as const]
   [clojure.string :as str]
   [clojure.test :as t]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-song-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization
                        :song db-data/song}]
      (let [query #:venia{:operation #:operation{:type :query
                                                 :name "SongByIdWithArtistAndMembers"}
                          :queries [[:song_by_id {:id :$id}
                                     [:id
                                      :name
                                      :artist_id
                                      [:artist
                                       [:id
                                        :type
                                        :name
                                        [:members
                                         [:id
                                          :name
                                          :organization_id
                                          :organization_name]]]]
                                      :release_date]]]
                          :variables [#:variable{:name "id"
                                                 :type :Int!}]}]
        (t/testing "アーティストが取得できる"
          (let [{:keys [status body]}
                (helper/run-query sys {:query query
                                       :variables {:id 1}})]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id {:id 1
                                          :name "君のこころは輝いてるかい？"
                                          :artist_id 1
                                          :artist {:id 1
                                                   :type 1
                                                   :name "Aqours"
                                                   :members [{:id 1
                                                              :name "黒澤 ダイヤ"
                                                              :organization_id 1
                                                              :organization_name "浦の星女学院"}
                                                             {:id 2
                                                              :name "渡辺 曜"
                                                              :organization_id 1
                                                              :organization_name "浦の星女学院"}
                                                             {:id 3
                                                              :name "津島 善子"
                                                              :organization_id 1
                                                              :organization_name "浦の星女学院"}]}
                                          :release_date "2015-10-07"}}}
                     (-> body helper/<-json))))
          (let [{:keys [status body]}
                (helper/run-query sys {:query query
                                       :variables {:id 100}})]
            (t/is (= 200 status))
            (t/is (= {:data {:song_by_id nil}}
                     (-> body helper/<-json)))))
        (t/testing "members選択なし"
          (let [query #:venia{:operation #:operation{:type :query
                                                     :name "SongByIdWithArtist"}
                              :queries [[:song_by_id {:id :$id}
                                         [:id
                                          :name
                                          :artist_id
                                          [:artist
                                           [:id
                                            :type
                                            :name]]
                                          :release_date]]]
                              :variables [#:variable{:name "id"
                                                     :type :Int!}]}]
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:id 1}})]
              (t/is (= 200 status))
              (t/is (= {:data {:song_by_id {:id 1
                                            :name "君のこころは輝いてるかい？"
                                            :artist_id 1
                                            :artist {:id 1
                                                     :type 1
                                                     :name "Aqours"}
                                            :release_date "2015-10-07"}}}
                       (-> body helper/<-json))))
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:id 100}})]
              (t/is (= 200 status))
              (t/is (= {:data {:song_by_id nil}}
                       (-> body helper/<-json))))))
        (t/testing "artistもmembersも選択なし"
          (let [query #:venia{:operation #:operation{:type :query
                                                     :name "SongByIdWithoutArtistAndMembers"}
                              :queries [[:song_by_id {:id :$id}
                                         [:id
                                          :name
                                          :artist_id
                                          :release_date]]]
                              :variables [#:variable{:name "id"
                                                     :type :Int!}]}]
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:id 1}})]
              (t/is (= 200 status))
              (t/is (= {:data {:song_by_id {:id 1
                                            :name "君のこころは輝いてるかい？"
                                            :artist_id 1
                                            :release_date "2015-10-07"}}}
                       (-> body helper/<-json))))
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:id 100}})]
              (t/is (= 200 status))
              (t/is (= {:data {:song_by_id nil}}
                       (-> body helper/<-json))))))))))

(t/deftest test-list-songs
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:artist db-data/artist
                        :artist_member db-data/artist_member
                        :member db-data/member
                        :organization db-data/organization
                        :song db-data/song}]
      (let [query #:venia{:operation #:operation{:type :query
                                                 :name "SongsWithArtistAndMembers"}
                          :queries [[:songs {:name :$name}
                                     [:id
                                      :name
                                      :artist_id
                                      [:artist
                                       [:id
                                        :type
                                        :name
                                        [:members
                                         [:id
                                          :name
                                          :organization_id
                                          :organization_name]]]]
                                      :release_date]]]
                          :variables [#:variable{:name "name"
                                                 :type :String}]}]
        (t/testing "バリデーションエラー"
          (t/testing "楽曲名空文字チェック"
            (t/testing "メンバー名空文字チェック"
              (let [{:keys [status body]}
                    (helper/run-query sys {:query query
                                           :variables {:name ""}})]
                (t/is (= 200 status))
                (let [{:keys [message extensions]} (helper/nth-errors body 0)]
                  (t/is (str/starts-with? message "name"))
                  (t/is (= const/error-code-validation (:code extensions))))))))
        (t/testing "楽曲の一覧が取得できる"
          (let [{:keys [status body]}
                (helper/run-query sys {:query query
                                       :variables {:name "輝"}})]
            (t/is (= 200 status))
            (t/is (= {:data {:songs [{:id 1
                                      :name "君のこころは輝いてるかい？"
                                      :artist_id 1
                                      :artist {:id 1
                                               :type 1
                                               :name "Aqours"
                                               :members [{:id 1
                                                          :name "黒澤 ダイヤ"
                                                          :organization_id 1
                                                          :organization_name "浦の星女学院"}
                                                         {:id 2
                                                          :name "渡辺 曜"
                                                          :organization_id 1
                                                          :organization_name "浦の星女学院"}
                                                         {:id 3
                                                          :name "津島 善子"
                                                          :organization_id 1
                                                          :organization_name "浦の星女学院"}]}
                                      :release_date "2015-10-07"}]}}
                     (-> body helper/<-json))))
          (let [{:keys [status body]}
                (helper/run-query sys {:query query
                                       :variables {:name "空"}})]
            (t/is (= 200 status))
            (t/is (= {:data {:songs []}}
                     (-> body helper/<-json))))
          (t/testing "members選択なし"
            (let [query #:venia{:operation #:operation{:type :query
                                                       :name "SongsWithArtist"}
                                :queries [[:songs {:name :$name}
                                           [:id
                                            :name
                                            :artist_id
                                            [:artist
                                             [:id
                                              :type
                                              :name]]
                                            :release_date]]]
                                :variables [#:variable{:name "name"
                                                       :type :String}]}]
              (let [{:keys [status body]}
                    (helper/run-query sys {:query query
                                           :variables {:name "輝"}})]
                (t/is (= 200 status))
                (t/is (= {:data {:songs [{:id 1
                                          :name "君のこころは輝いてるかい？"
                                          :artist_id 1
                                          :artist {:id 1
                                                   :type 1
                                                   :name "Aqours"}
                                          :release_date "2015-10-07"}]}}
                         (-> body helper/<-json))))
              (let [{:keys [status body]}
                    (helper/run-query sys {:query query
                                           :variables {:name "空"}})]
                (t/is (= 200 status))
                (t/is (= {:data {:songs []}}
                         (-> body helper/<-json))))))
          (t/testing "artistもmembersも選択なし"
            (let [query #:venia{:operation #:operation{:type :query
                                                       :name "SongsWithoutArtistAndMembers"}
                                :queries [[:songs {:name :$name}
                                           [:id
                                            :name
                                            :artist_id
                                            :release_date]]]
                                :variables [#:variable{:name "name"
                                                       :type :String}]}]
              (let [{:keys [status body]}
                    (helper/run-query sys {:query query
                                           :variables {:name "輝"}})]
                (t/is (= 200 status))
                (t/is (= {:data {:songs [{:id 1
                                          :name "君のこころは輝いてるかい？"
                                          :artist_id 1
                                          :release_date "2015-10-07"}]}}
                         (-> body helper/<-json))))
              (let [{:keys [status body]}
                    (helper/run-query sys {:query query
                                           :variables {:name "空"}})]
                (t/is (= 200 status))
                (t/is (= {:data {:songs []}}
                         (-> body helper/<-json))))))
          (t/testing "絞り込み条件指定なし"
            (let [{:keys [status body]}
                  (helper/run-query sys {:query #:venia{:queries [[:songs
                                                                   [:name
                                                                    [:artist
                                                                     [:name
                                                                      [:members
                                                                       [:name]]]]]]]}})]
              (t/is (= 200 status))
              (t/is (= {:data {:songs [{:name "君のこころは輝いてるかい？"
                                        :artist {:name "Aqours"
                                                 :members [{:name "黒澤 ダイヤ"}
                                                           {:name "渡辺 曜"}
                                                           {:name "津島 善子"}]}}
                                       {:name "元気全開DAY！DAY！DAY!"
                                        :artist {:name "CYaRon!"
                                                 :members [{:name "渡辺 曜"}]}}
                                       {:name "トリコリコPLEASE!!"
                                        :artist {:name "AZALEA"
                                                 :members [{:name "黒澤 ダイヤ"}]}}
                                       {:name "Strawberry Trapper"
                                        :artist {:name "Guilty Kiss"
                                                 :members [{:name "津島 善子"}]}}
                                       {:name "SELF CONTROL!!"
                                        :artist {:name "Saint Snow"
                                                 :members [{:name "鹿角 理亞"}]}}
                                       {:name "Awaken the power"
                                        :artist {:name "Saint Aqours Snow"
                                                 :members [{:name "黒澤 ダイヤ"}
                                                           {:name "渡辺 曜"}
                                                           {:name "津島 善子"}
                                                           {:name "鹿角 理亞"}]}}]}}
                       (-> body helper/<-json))))))))))
