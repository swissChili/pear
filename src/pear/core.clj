(ns pear.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [digest]
            [ring.adapter.jetty]))

(def blockchain (atom []))
(def peers (atom []))
(def genesis-block {
  :nonce 0
  :hash "GENESIS"
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
          (println "CHain Hash: " chain-hash)
          (println "Block Hash: " (:hash last-block))
          (if (= chain-hash (:hash last-block))
            (do
            (println "IF evaluated to true" (:hash last-block))
            (validate new-chain))
            false))))
  (if (= genesis-block
        (first @chain))
    (do
      true)
    false))) ;; if it is first, return true

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
        :body (json/write-str @peers )}
    :else 
      { :status 404
        :headers {"Content-Type" "text/plain"}
        :body "error 404: path not found. Try /chain || /peers"}))))

(defn -main
  "Initiate blockchain"
  [& args]
  (append-block blockchain genesis-block)
  ;;(println (json/write-str
    ;;@(add-block blockchain "ried" "ur mum big bad dumb")))
  ;;(println @blockchain)
  ;;(println "Validating blockchain..." (validate blockchain)))
  (ring.adapter.jetty/run-jetty handler {:port 3000}))