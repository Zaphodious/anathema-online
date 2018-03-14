(ns anathema-online.routes
  (:require [clojure.java.io :as io]
            [compojure.core :refer [ANY GET PUT POST DELETE routes context]]
            [compojure.route :refer [resources]]
            [anathema-online.disk :as adisk]
            [ring.util.response :as resp :refer [response]]
            [anathema-online.data :as data]
            [anathema-online.creation :as creation]
            [anathema-online.disk :as disk]
            [clojure.string :as str]
            [rum.core :as rum]))

(defn master-key-present? [{environ-key :masterkey}
                           {{:strs [masterkey environ]} :headers :as request}]
  (= environ-key masterkey))

(defn de-pluralize [plural-category]
  (cond
    (= '(\i \e \s) (->> plural-category reverse (take 3) reverse)) (str/replace plural-category "ies" "y")
    (= \s (last plural-category)) (->> plural-category drop-last (reduce str))))

(defn home-routes [{:keys [disk environ] :as endpoint}]
  (routes
    (GET "/data/players/:key.full.:filetype" [key filetype]
      (-> (disk/get-for-player disk key)
          (data/write-data-as (keyword filetype))
          resp/response
          (resp/content-type (data/content-type-for filetype))))
    (GET "/data/:category/:key.:filetype" [category :<< de-pluralize, key filetype]
      (-> (adisk/read-object
            disk
            [(keyword category)
             key])
          (data/write-data-as (keyword filetype))
          resp/response
          (resp/content-type (data/content-type-for filetype))))
    (GET "/bla/" [:as {{:strs [masterkey]} :headers :as a}]
      (str (pr-str environ) (master-key-present? environ a)))
    (POST "/data/new/:category" [category :as request]
      (let [new-key (data/new-id)
            new-thing (creation/new-thing (keyword category) "Unnamed" new-key false)
            write-result (when new-thing (disk/write-object! disk new-thing))]
        (if new-thing
          (resp/created (str "/data/fetch/" category "/" new-key ".edn"))
          (if (= :player (keyword category))
            (assoc (resp/not-found "Please use the sign-up page to create a new player.")
              :status 403)
            (resp/not-found (str "No support exists for creating new " category "-type entities."))))))


    (GET "/" _
      (-> "public/index.html"
          io/resource
          io/input-stream
          response
          (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))
    (resources "/")))