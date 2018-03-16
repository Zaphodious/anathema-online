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

(rum/defc design-test-frame [_]
  [:#app-frame
   [:.page-title
    [:h1 "Testing This"]]
   [:#menu [:ul [:li "Home"]
            [:li "Settings"]
            [:li "My Profile"]
            [:li "My Characters"]
            [:li "My Rulebooks"]]]
   [:#content

    [:.page
     [:.section
      [:h3 "Testing 123"]
      [:.interior
       [:ul
        [:li.field [:label "Text Field"] [:input {:type :text, :value "Stuff In Box"}]]
        [:li.field [:label "Big Text Field"] [:textarea {:value "Thingy In \n The \n Box"}]]]]]]]])
