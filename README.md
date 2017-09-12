Extirpater
==========

Requirements
------------
- Android KitKit 4.4.4 and higher

Instructions
------------
0. (Optional) Delete unnecessary files, clear histories, clear app cache, etc.
1. Launch the app
2. (Optional) Change the erase options from the menu
3. Click "Start" on either storage location
4. The status of drives are shown via the status label and progress bar

Known Issues
------------
- On devices without real external storage, the two shown are both internal
- The file table of the secondary drive will never really be filled
- The last 20MB aren't erased

Limitations
-----------
- Due to how flash drives work and the partition layout of Android devices, it'll never be possible to fully fill the drive

Planned Updates
---------------
- Better GUI
- Add a fast csprng data source
- Root support for filling /cache and /system
- Root support for fstrim'ing partitions

Goals
-----
- Be fast
- Don't eat batteries
- Use minimal permissions
- Use libraries only when necessary

Credits
-------
- Uncommons Maths, License: Apache 2.0, https://maths.uncommons.org
