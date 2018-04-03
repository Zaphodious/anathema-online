(ns anathema-online.service.core
  (:require [hireling.core :as hireling]))

(defn service-start []
  (println "Service Worker Started Successfully :D:D:D:D"))

(hireling/start-service-worker!
  {:version 1
   :cache-name "anathema-cache"
   :cached-paths {:cache-fastest
                  ["" "css/style.css" "css/font.css"
                   "https://fonts.googleapis.com/icon?family=Material+Icons"
                   "js/compiled/anathema_online.js"
                   "favicon.png"]}})