(ns aqoursql.resolver.members-test
  (:require [aqoursql.resolver.members :as sut]
            [aqoursql.test-helper.core :as helper :refer [with-db-data with-system]]
            [aqoursql.test-helper.db-data :as db-data]
            [clojure.test :as t]))

(t/use-fixtures
  :once
  helper/instrument-specs)

(t/deftest test-fetch-member-by-id
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:member db-data/member
                        :organization db-data/organization}]
      (let [query #:venia{:operation #:operation{:type :query
                                                 :name "MemberById"}
                          :queries [[:member_by_id {:id :$id}
                                     [:id
                                      :name
                                      :organization_id
                                      :organization_name]]]
                          :variables [#:variable{:name "id"
                                                 :type :Int!}]}]
        (t/testing "取得できる"
          (let [{:keys [status body]}
                (helper/run-query sys {:query query
                                       :variables {:id 1}})]
            (t/is (= 200 status))
            (t/is (= {:data {:member_by_id {:id 1
                                            :name "黒澤 ダイヤ"
                                            :organization_id 1
                                            :organization_name "浦の星女学院"}}}
                     (-> body helper/<-json)))))
        (t/testing "存在しないIDを指定すると取得できない"
          (let [{:keys [status body]}
                (helper/run-query sys {:query query
                                       :variables {:id 100}})]
            (t/is (= 200 status))
            (t/is (= {:data {:member_by_id nil}}
                     (-> body helper/<-json)))))))))

(t/deftest test-list-members
  (with-system [sys (helper/test-system)]
    (with-db-data [sys {:member db-data/member
                        :organization db-data/organization}]
      (t/testing "絞り込み条件ありの場合"
        (let [query #:venia{:operation #:operation{:type :query
                                                   :name "Members"}
                            :queries [[:members {:name :$name
                                                 :organization_name :$organization_name}
                                       [:id
                                        :name
                                        :organization_id
                                        :organization_name]]]
                            :variables [#:variable{:name "name"
                                                   :type :String}
                                        #:variable{:name "organization_name"
                                                   :type :String}]}]
          (t/testing "取得できる"
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:name "ダイヤ"}})]
              (t/is (= 200 status))
              (t/is (= {:data {:members [{:id 1
                                          :name "黒澤 ダイヤ"
                                          :organization_id 1
                                          :organization_name "浦の星女学院"}]}}
                       (-> body helper/<-json))))
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:organization_name "函館"}})]
              (t/is (= 200 status))
              (t/is (= {:data {:members [{:id 4
                                          :name "鹿角 理亞"
                                          :organization_id 2
                                          :organization_name "函館聖泉女子高等学院"}]}}
                       (-> body helper/<-json)))))
          (t/testing "該当なしの条件を指定すると空リスト"
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:name "ルビィ"}})]
              (t/is (= 200 status))
              (t/is (= {:data {:members []}}
                       (-> body helper/<-json))))
            (let [{:keys [status body]}
                  (helper/run-query sys {:query query
                                         :variables {:organization_name "沼津"}})]
              (t/is (= 200 status))
              (t/is (= {:data {:members []}}
                       (-> body helper/<-json)))))))
      (t/testing "絞り込み条件なしの場合"
        (let [{:keys [status body]}
              (helper/run-query sys {:query #:venia{:queries [[:members
                                                               [:name]]]}})]
          (t/is (= 200 status))
          (t/is (= {:data {:members [{:name "黒澤 ダイヤ"}
                                     {:name "渡辺 曜"}
                                     {:name "津島 善子"}
                                     {:name "鹿角 理亞"}]}}
                   (-> body helper/<-json))))))))
