(ns anathema-online.ui
  "Defines entry point api for working with the UI system. Includes functions to help in dealing with UI System Components.

  Each UI System Component contains:
    :initial-component -
        The function for the initial component that the system will render.
        Arity-2, receiving map of {:change-fn x, :view-atom a} (not necessarily the UI System Component) and the current viewmap.
    :disk - System Disk Component.
    :change-fn - (fn [viewmap-for-desired-change] return value not used). Stateful in browser, impotent on server but present to prevent nil pointer exceptions.
    :view-atom - 'Read-Only' atom showing current view (for the 'reactive' rum mixin). Impotent on server same as change-fn.
    :dom-root - the DOM root. Stub for server, actual root for browser."
  (:require [anathema-online.ui.pages :as pages]
            [anathema-online.ui.fields :as fields]
            [anathema-online.ui.frame :as ui.frame]
            [rum.core :as rum]
            [anathema-online.disk :as disk]))

(def fallback-initial-component ui.frame/design-test-frame)

(defn get-comp-fn
  "[ui-component, viewmap representing what to display]
   On the server, returns 0-arity function returning a map of {:react-markup, :vanilla-markup}.
   In the browser, returns a 0-arity function that returns the component for the present view.
"
  [{:keys [initial-component dom-root]
    system-disk :disk
    :as this}]
  #?(:clj (fn []
            {:react-markup (rum/render-html (initial-component this))}
            :vanilla-markup (rum/render-static-markup (initial-component this)))
     :cljs #(initial-component this)))

(defn make-update-fn [{:keys [disk]
                       :as this}]
  (fn [{:keys [path view]}]
    (disk/change-object! disk path (constantly view))))

(defn init-with-update-fn [this]
  (assoc this :change-fn (make-update-fn this)))
