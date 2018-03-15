(ns anathema-online.ui.frame
  (:require [rum.core :as rum]))

(rum/defc frame [{:keys [view-atom change-fn]
                  :as ui-component}]
  [:#app-frame
   [:#menu [:ul [:li "Home"]
            [:li "Settings"]
            [:li "My Profile"]
            [:li "My Characters"]
            [:li "My Rulebooks"]]]
   [:#content [:p "Thing here!"]]])