(ns anathema-online.ui.pages)

(defmulti page-for-category
          (fn [entity ui-component]
            (:category entity)))