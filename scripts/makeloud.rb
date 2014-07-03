#!/usr/bin/env ruby

loud_files = ['jacuzzi','sign','highway','bumblebee','fork','train','cat',
  'lightbulb','chainsaw','microwave','eraser','sirens','modem','poster',
  'helicopter','zipper','soda','factory','stapler','oven','coyote','monk',
  'bottle','muffler','lullaby','cotton','drums','chimes','pad','airport',
  'breeze','disc','club','lamb','wool','crow','thief','cup','fireworks',
  'aquarium','deodorant','tractor','humidifier','book','blender','steam',
  'bill','stereo','pompoms','pillow','alarm','fan','calendar','thunder',
  'wasp','cable','gun','meadow','coat','mob','windmill','wrench','speedway',
  'museum','compass','atv','brook','goggles','shredder','elevator',
  'thermometer','zoo','study','hall','brush','bangles']

unless ARGV.length == 2
  puts "makeloud.rb <from> <to>"
  exit 1
end

from = ARGV[0]
to = ARGV[1]
lcount = 0
scount = 0

path = File.join(Dir.pwd, to)

Dir.chdir(from)
Dir.glob('**/*.wav').each do |file|
  m = file.match(%r(.*/([^ ]*) ?M?F? ?L?.wav))
  unless m
    puts file
    puts "no match?"
  end
  if loud_files.include?(m[1].downcase)
    #system "ffmpeg -i '#{file}' -af 'volume=10.0' '#{File.join(path, file)}'"
  else
    system "cp '#{file}' '#{File.join(path, file)}'" 
  end
end