# Window
Wraps Webview. User need to add it to a Sense.
Provides JSCallable Classes, Keeps Observer Registry
# Node
Root Object, Keeps Connection Object.

# Pointer
Extendable Class extending Node and carring code to
get and set. Only Trigger if java calls

# Observer
Extends Node.
Extendable Class that can tie to Dom Events on Global or in a Element, exposes
events to execute code on Triggers, one
observer can have multiple lambdas.

# Element
Extends node. Have Methods extending js Execution.
Have Children, and synced with Dom if connected.
this have abstract methods of Generating Html, css, js.
it returns a Data Model.

---
# Communication Model
## JS Execution Calls
JS Execution calls go through js Escape,
- Strings
- id's
- paths

then go through executeScript and returns value.

## Event calls
each Observer has unique UUID and ID, in short each event in a element
have unique UUID. on Attach UUID will be saved in 
java.eventCall(UUID,Object) call and window saves UUID+Observer. js template is created inside
Observer Object and it can be overrided.

## Batching
ExecuteJS and ExecuteChildJS can be batched. batching
stops current executions and saves it into a List.
Both methods will be independent from each other
and both live in Node.

## element Handling
there are defined element that contain structure for
HTML Skevilten. that marked as the WindowRoot and other
elements need to add into that.

## Style Handling
we have two type of styling
### Programmatic styles 
Styles have three main properties.
target, group and Styles. at Building this returns
StyleDef with target,Group and styles. at window
those same group styles forms one style tags.
that groups can accessed like a dom element.
### Style Files
User can directly add them to WindowRoot like a normal
element.


## Event Handling
there are three event types.
### java events
works in Observers and managed via registry in node
### js events
Binds directly to js as a JS event.
### css events
adds as a style event and later adds on load.

---
# ChildJs execution
in childJs call element passes childJs string to
node. then node wraps it in a IFFE and executes it.
in a batch that element call is stored in a List
and all element calls adds into one IFFE in commit.

# JS execution
this also work similar to childjs.

> Both throw NotAttachedException and JSException

---
# Events
## Java Events
in java events we initially use Observer in a element
internally, because of it has it's own windowObject
it need to be handled carefully.
we can use,
```
btn.onClick((e)->{
    
});
btn.addEvent("Click",(e)->{});
```
when event was made it goes to Node and Node store
and execute it for binding. if component not attached
it saves inside a list for later execution. in js
side there is a map with UUID,function to removeEvent

1. executing event
   
    node gets event function, creates function with
variable name inside IFFE.
```
event = function(e){}
ele.addEvent("evtType",event);
saveEvent(UUID,event);
```
then later unregister events using that Map.

2. calling event

    in event call java.executeEvent called from 
bound eventlistner. then node checks UUID in reg
and calls to execute the event in ovserver

## js Events
js events constructed in element and moved to
executeChildJs for execution. if user didn't add
it to event registry it cannot be inregistered.

## css events
those are css psudo classes and cannot be unregistered
directly. but group can be disabled as will.
---
# css blocks
Wrapper mainly create css styleblocks with a groupid
and build step collects all groups.
css block have few properties
- ```List<Target(StyleRef[])> pointers```

StyleRef is a class that can hold one element
and one psudoEvent
 - (".btn","hover")

Target is a container that holds pointers.
- caontains pack and also method chain

In pointers inner List contains path to element
and outer list contains different elements connected.
as example
 - ((".btn","hover"),(".item"))("html") creates .btn:hover .item , html

css block have methods that can inject other pointers
normally registarting look like this
```
Styleblock.of("anim",Hover(".btn").main("item"),main("html")).add(key,val);
```
styleblock contains methods to manipulate this.
- addTarget lets add a another target to block
- packAll(StyleRef) lets add parent StyleRef to all targets
- group is final and let users to assign styles into groups

in Build, styleblocks passed to node and it not marge anything and send to
each group to separate css style tag.