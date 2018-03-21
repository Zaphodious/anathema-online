(ns anathema-online.ui.frame
  (:require [rum.core :as rum]
            [anathema-online.ui.fields :as fields]))

(rum/defc frame [{:keys [view-atom change-fn]
                  :as ui-component}]
  [:#app-frame
   [:#menu [:ul [:li "Home"]
            [:li "Settings"]
            [:li "My Profile"]
            [:li "My Characters"]
            [:li "My Rulebooks"]]]
   [:#content [:p "Thing here!"]]])

(def demo-atom (atom {:name "What Makes you tick."
                      :description "I Like Pie! \n A Lot!!! \n\n A WHOLE LOT!!!"}))

(def demo-section
  [:.section
   [:h3 "Testing 123"]
   [:.interior
    [:ul
     [:li.field [:label "Text Field"] (fields/form-field-for
                                        {:field-type :text
                                         :path [:name]
                                         :read-only false
                                         :change-fn (fn [a] a)
                                         :view-atom demo-atom})]
     [:li.field [:label "Big Text Field"] (fields/form-field-for
                                            {:field-type :big-text
                                             :path [:description]
                                             :read-only false
                                             :change-fn (fn [a] a)
                                             :view-atom demo-atom})]]]])


(rum/defc design-test-frame [_]
  [:#app-frame
   [:.page-title
    [:h1 "Testing This"]]
   [:#menu
    [:ul [:li [:i.material-icons.menu-icon "apps"] [:span.label "Home"]]
         [:li [:i.material-icons.menu-icon "settings"] [:span.label "Settings"]]
         [:li [:i.material-icons.menu-icon "storage"] [:span.label "My Profile"]]
         [:li [:i.material-icons.menu-icon "face"] [:span.label "My Characters"]]
         [:li [:i.material-icons.menu-icon "book"] [:span.label "My Rulebooks"]]]]
   [:#content

    [:.page (take 12 (repeat demo-section))]]])
