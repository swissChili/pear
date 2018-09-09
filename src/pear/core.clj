(ns pear.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [digest]
            [ring.adapter.jetty]
            [seesaw.core :refer :all]
            [org.httpkit.client :as http]))

(def port 23952)

(def blockchain (atom []))
(def peers (atom []))

(def genesis-block {
  :nonce 0
  :hash ""
  :sender "God"
  :message "Let there be light!"
})
(defn append-block
  "appends a block to the chain"
  [chain block]
  (swap! chain conj block))

(defn add-block
  "This adds a block to the chain"
  [chain sender message]
  (let [hash (digest/md5 (json/write-str @chain))]
    (let [block {
        :nonce 0
        :message message
        :sender sender
        :hash hash
      }]
      (append-block chain block))
  chain))

(defn validate
  "this validates the chain by checking the last block and
  recursively checking all before it"
  [chain]
  (if (> (count @chain) 1) ;; if this is not the first block
                           ;; check all previous blocks
    (let [new-chain (atom (drop-last @chain))]
      (let [last-block (last @chain)]
        (let [chain-hash (digest/md5 (json/write-str @new-chain))]
          (if (= chain-hash (:hash last-block))
            (do
            (validate new-chain))
            ;; return false if hash is invalid
            false))))
  (if (= genesis-block
        (first @chain))
    true     ;; if it is first, return true
    false))) ;; return false if first block not genesis

;; server logic
(defn handler
  "handles incoming requests and returns appropriat info"
  [request]
  (do
  (let [url (:uri request)]
  (cond
    (= url "/chain") 
      { :status 200
        :headers {"Content-Type" "text/json"}
        :body (json/write-str @blockchain) }
    (= url "/peers")
      { :status 200
        :headers {"Content-Type" "text/json"}
        :body (json/write-str @peers) }
    (= url "/updated")
      (do
        ; get requesters chain
        (defn request-update 
          "check all the peers for new chain instances"
          [peer-index]
          (let [request-chain (slurp
            (apply str
              ["http://" (:host (nth peer-index @peers) ":" port "/chain")]))]
          )
          (if (> (count @peers) 0)
            (request-update (+ peer-index 1))
            { :status 200
              :headers {"Content-Type" "text/plain"}
              :body "added to peer list"}))
        (if (> (count peers) 0)
          (request-update 0)
          { :status 500
            :headers {"Content-Type" "text/plain"}
            :body "no peers"}))
    (= url "/register")
      (do
        (println request)
        (let [peer {:host (:remote-addr request)}]
          (append-block peers peer)
          (println @peers)
        { :status 200
          :headers {"Content-Type" "text/json"}
          :body (json/write-str @blockchain) }))
    :else
      { :status 404
        :headers {"Content-Type" "text/plain"}
        :body "error 404: path not found. Try /chain || /peers" }))))

(defn join-chain
  "sends a register request to a specified IP"
  [ip]
  (let [registered-chain (slurp (apply str ["http://" ip ":" port "/register"]))]
    (println ip registered-chain)))

(defn share-chain
  "shares the updated chain with all peers"
  [chain]
  )

(defn start-ui
  "starts up the user interface so I dont clutter -main"
  []
  (def f (frame :title "Pear"))
  (defn display
    [content]
    (config! f :content content)
    content)
  (def messages (listbox :model (-> (map (fn [x] (:message x)) @blockchain))))
  (def connect-button (button :text "Connect via IP"))
  (listen connect-button :action (fn [e]
                                   (alert e "Clonked")))
  (def message-field (display (text "Message:")))
  (def left-panel (flow-panel
                   :align :left
                   :hgap 20
                   :items [
                           connect-button
                           ]))
  (def messages-panel (flow-panel
                       :align :left
                       :hgap 20
                       :items [
                               messages
                               message-field
                               ]))
  (def split (left-right-split (scrollable left-panel) (scrollable messages-panel)
                               :divider-location 1/3))
  (display split)
  (invoke-later
    (-> f pack! show!)))

(defn update-list
  "updates the listbox with the new blockchain"
  [listbox chain]
  (println chain)
  (seesaw.core/config! listbox :model (map (fn [x] (:message x))@chain)))

(defn create-message
  "creates a message in the chain and updates UI"
  [message]
  (add-block
   blockchain
   (slurp "http://icanhazip.com")
   message)
  (update-list messages blockchain)
  )

(defn -main
  "Initiate blockchain"
  [& args]
  (append-block blockchain genesis-block)
  (println "started pear-server on" (slurp "http://icanhazip.com") "port" port)
  ;;(println (json/write-str
    ;;@(add-block blockchain "ried" "ur mum big bad dumb")))
  ;;(println @blockchain)
  ;;(println "Validating blockchain..." (validate blockchain)))
  ;; start ui
  (start-ui)
  (ring.adapter.jetty/run-jetty handler {:port port}))
