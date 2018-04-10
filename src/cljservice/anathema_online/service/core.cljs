(ns anathema-online.service.core
  (:require [hireling.core :as hireling]
            [anathema-online.route-def :as ard]
            [bidi.bidi :as bidi]
            [clojure.string :as str]))

(defn service-start []
  (println "Service Worker Started Successfully :D:D:D:D"))

(def index-route (bidi/path-for ard/routemap ::ard/index))

(hireling/start-service-worker!
  {:version 1
   :app-name "anathema"
   :precaching [{:url index-route
                 :revision 1}
                {:url "/favicon.png"
                 :revision 1}]
   :navigation-route {:url index-route
                      :whitelist [#"/character/"
                                  #"/chron/"]
                      :blacklist [#"/data/"
                                  #"/img/"
                                  #"/css/"
                                  #"/fonts/"
                                  #"/js/"]}
   :precache-routing-opts {:directoryIndex "index.html"}
   :cache-routes [{:strategy :stale-while-revalidate
                   :cache-name "jscache"
                   :route #".js"}
                  {:strategy :stale-while-revalidate
                   :cache-name "csscache"
                   :route #".css"}
                  {:strategy :stale-while-revalidate
                   :cache-name "fontcache"
                   :route #"/fonts/"}
                  {:strategy :stale-while-revalidate
                   :route #".png|.jpg|.gif"
                   :cache-name "imgcache"
                   :max-age-seconds (* 60 60)}
                  {:strategy :network-first
                   :cache-name "datacache"
                   :route #"/data/"}]})

(.skipWaiting js/workbox)
(.clientsClaim js/workbox)