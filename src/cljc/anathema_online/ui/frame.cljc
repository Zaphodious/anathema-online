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

(def demo-section
  [:.section
   [:h3 "Testing 123"]
   [:.interior
    [:ul
     [:li.field [:label "Text Field"] [:input {:type :text, :value "Stuff In Box"}]]
     [:li.field [:label "Big Text Field"] [:textarea {:value "Thingy In \n The \n Box"}]]]]])


(rum/defc design-test-frame [_]
  [:#app-frame
   [:.page-title
    [:h1 "Testing This"]]
   [:#menu
    [:.tab.material-icons "menu"]
    [:ul [:li [:i.material-icons.menu-icon "apps"] "Home"]
         [:li [:i.material-icons.menu-icon "settings"] "Settings"]
         [:li [:i.material-icons.menu-icon "storage"] "My Profile"]
         [:li [:i.material-icons.menu-icon "face"] "My Characters"]
         [:li [:i.material-icons.menu-icon "book"] "My Rulebooks"]]]
   [:#content

    [:.page (take 12 (repeat demo-section))]]])
