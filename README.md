MIDetectorOSC
=============
*Last tested on SuperCollider 3.6.3 on Ubuntu 13.04*

Supercollider extension Quark-wannabe for relaying Music Information via OSC.
The main motivation behind this quark is to have connect visual interactions with sound interactions via OSC Messaging.
Ideally you would run the detector in SC and relay the info to your favorite program like Processing, OF, Cinder, etc.

How to install and use
-------------------------
<ol>
<li>git clone to the to the Supercollider/quarks folder, typically this is
<ul>
  <li> Linux:  ~/.local/share/SuperCollider/</li>
  <li>MacOSX: ~/Library/Application\ Support/SuperCollider/ </li>
  <li>Windows: C:\Users\UserName\SuperCollider\</li>
</ul>
So this would be...
<br>
<code>
cd folder
</code>
<br>
<code>
git clone https://github.com/beangoben/MIDetectorOSC.git
</code>

</li>
<li>Copy MIDetectorOSC.quark to the Supercollider/quarks/DIRECTORY folder.</li>
<li>You can now install via Quarks.gui.</li>
<li>Checkout the MIDetectorOSC guide/overview helpfile.</li>
</ol>


Wish/Todo-list:
-------------------------

* Solve FIFO errors (these appear after 1 or 2 hours running).
* Correct ON/OFF button.
* Correct Multi-Channel Tagging.
* Integrate the Net part to the Manager.
* Generalize SynthDef creation for N-channels.
* More detectors: FFTbins, FFT Centroids, Specialized Onset Detectors.
* Make awesome documentation and examples with processing.
* Calculate statistical info like medians, max,min, averages and variance.

Want to contribute with code? Have any Ideas? Problems?
-------------------------
Sendme an email!  beangoben@gmail.com


