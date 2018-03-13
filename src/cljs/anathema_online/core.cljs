(ns anathema-online.core
  (:require [rum.core :as rum]
            [anathema-online.system :as asys]))

(enable-console-print!)

(defonce app-state (atom {:text "Hello Chestnut!"}))

(rum/defc greeting < rum/reactive []
   [:h1 (:text (rum/react app-state))])

(defn render []
  (rum/mount (greeting) (. js/document (getElementById "app"))))

(asys/go)