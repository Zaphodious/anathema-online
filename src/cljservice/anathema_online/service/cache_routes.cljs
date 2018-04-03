(ns anathema-online.service.cache-routes
  (:require [bidi.bidi :as bidi]))

(def route-structure
  [:get
   ["/" {""               :home
         ["css/" :docname] :css
         ["js/" :docname]  :js
         ["img/" :docname] :img
         ["img/abilities/" :docname] :ability-image
         "data/" {#{[:category "/" :key "." :opt "." :ext]
                    [:category "/" :key "." :ext]}
                  :entity}}]])