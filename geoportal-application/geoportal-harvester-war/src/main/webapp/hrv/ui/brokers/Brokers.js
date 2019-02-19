/* 
 * Copyright 2016 Esri, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dijit/_WidgetsInTemplateMixin",
        "dojo/i18n!../../nls/resources",
        "dojo/text!./templates/Brokers.html",
        "dojo/_base/lang",
        "dojo/_base/array",
        "dojo/on",
        "dojo/html",
        "dojo/dom-construct",
        "dojo/json",
        "dojo/topic",
        "dijit/Dialog",
        "dijit/form/Button",
        "hrv/rest/Brokers",
        "hrv/ui/brokers/Broker",
        "hrv/ui/brokers/BrokerEditorPane"
      ],
  function(declare,
           _WidgetBase,_TemplatedMixin,_WidgetsInTemplateMixin,
           i18n,template,
           lang,array,on,html,domConstruct,json,topic,
           Dialog,Button,
           BrokersREST,Broker,BrokerEditorPane
          ){
  
    return declare([_WidgetBase, _TemplatedMixin, _WidgetsInTemplateMixin],{
      i18n: i18n,
      templateString: template,
      widgets: [],
    
      postCreate: function(){
        this.inherited(arguments);
        html.set(this.captionNode,this.i18n.brokers[this.category]);
        this.load();
      },
      
      destroy: function() {
        this.inherited(arguments);
        this.clear();
      },
      
      clear: function() {
        array.forEach(this.widgets, function(widget){
          widget.destroy();
        });
        this.widgets = [];
        topic.publish("msg");
      },
      
      load: function() {
        BrokersREST[this.category]().then(
          lang.hitch(this,this.processBrokers),
          lang.hitch(this,function(error){
            console.error(error);
            topic.publish("msg", new Error(this.i18n.brokers.errors.access));
          })
        );
      },
      
      processBrokers: function(response) {
        response = response.sort(function(a,b){
          var t1 = a.brokerDefinition.label;
          t1 = t1? t1.toLowerCase(): "";
          var t2 = b.brokerDefinition.label;
          t2 = t2? t2.toLowerCase(): "";
          if (t1<t2) return -1;
          if (t1>t2) return 1;
          return 0;
        });
        this.clear();
        array.forEach(response,lang.hitch(this,this.processBroker));
      },
      
      processBroker: function(broker) {
        var widget = new Broker(broker).placeAt(this.contentNode);
        this.widgets.push(widget);
        
        widget.load = lang.hitch(this,this.load);
        this.own(on(widget,"remove",lang.hitch(this,this._onRemove)));
        this.own(on(widget,"edit",lang.hitch(this,this._onEdit)));
        widget.startup();
      },
      
      _onAdd: function(evt) {
        // create editor pane
        var brokerEditorPane = new BrokerEditorPane({
          category: this.category==="input"? "inbound": this.category==="output"? "outbound": null,
          data: null
        });
        
        // create editor dialog box
        var brokerEditorDialog = new Dialog({
          title: this.i18n.brokers.editor.caption,
          content: brokerEditorPane,
          class: "h-broker-editor",
          onHide: function() {
            brokerEditorDialog.destroy();
            brokerEditorPane.destroy();
          }
        });
        this.own(brokerEditorPane);
        this.own(brokerEditorDialog);
        
        // listen to "submit" button click
        this.own(on(brokerEditorPane,"submit",lang.hitch(this, function(evt){
          var brokerDefinition = evt.brokerDefinition;
          
          // use API create new broker
          BrokersREST.create(json.stringify(brokerDefinition)).then(
            lang.hitch({brokerEditorPane: brokerEditorPane, brokerEditorDialog: brokerEditorDialog, self: this},function(){
              this.brokerEditorDialog.destroy();
              this.brokerEditorPane.destroy();
              this.self.load();
            }),
            lang.hitch(this,function(error){
              console.error(error);
              topic.publish("msg", new Error(this.i18n.brokers.errors.creating));
            })
          );
        })));
        
        brokerEditorDialog.show();
      },
      
      _onRemove: function(evt) {
        var uuid = evt.data.uuid;
        
        // use API to remove broker
        BrokersREST.delete(uuid).then(
          lang.hitch(this,function(){
            this.load();
          }),
          lang.hitch(this,function(error){
            console.error(error);
            topic.publish("msg", new Error(this.i18n.brokers.errors.creating));
          })
        );
      }
    });
});
