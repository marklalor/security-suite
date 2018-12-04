alphabet = ('A'..'Z').to_a
dictionary = {}
File.open("words.txt").each do |line|
    dictionary[line.upcase.strip] = true
end

cipher_text = "tx iof elq prdx at gtqqg, ve amg gsi icedx at gtqqg, ve amg gsi mur zj iwfosy, wg hee hup ess bq jacytwtbrdw, uh jlw fvr ptaqu zj nsytir, wg hee hup ibcps sr wanvqrhwmfm, ve amg gsi esndsz cs wmsvg, tx iof elq grlwab bq hmfxyieg, ve amg gsi edetrs cs ssbs, ve amg gsi iwaeid cs oiedntv, is ulh qjrccfvvyk nsszvq if, hi toq ysfvvyk nsszvq if, hi isep exz tzmzu qtvqqg es tsngiz, kr hids nwp scvyk pwepgf hup sfvrc amm – vy wtcee, xts cpvucq hee gb qed zvvi fvr avqgryx bsetsp, hulx eczp sr wgd rawftieh nfxtcetxusf trewfeip ca txe prtrs frniujro, jaf tzsp ce qsd sitp, ub gsi eicpvxogtzq rrrvqs bq gaaclvugby szzl."
puts cipher_text
puts
puts "You're given the above text, encrypted using a Vigenere Cypher with unkown key, and you want to decrypt it."
gets
text = cipher_text.upcase.delete("^a-zA-Z")
offsets = []
for i in 0..(text.length - 3) do
	sub = text[i, 3]
	index = text.index(sub, i + 1)
	while index != nil
		offsets << index - i
		index = text.index(sub, index + 1)
	end
end

puts offsets.join(" ")

print "Period guess: "
input = gets.strip.to_i

chunks = []
i = 0
while i < text.length do
	chunks << text[i, input]
	i += input
end

letter_counts = []
possible_letters = []

for i in 0..(input-1) do
	letter_count = {}
	possible_letter = []
	chunks.each do |word|
		letter = word[i]
		if letter_count.key?(letter)
			letter_count[letter] = letter_count[letter] + 1
		else
			letter_count[letter] = 1
		end
	end
	letter_count = letter_count.sort_by{ |letter, count| count}.reverse
	letter_counts << letter_count
	puts "Letter #{i + 1} count: "
	letter_count.each{|key, pair| print "#{key}: #{pair} \t"}
	puts
	for i in 0..2 do
		possible_letter << alphabet[(alphabet.find_index(letter_count[i].first) + 22) % 26]
	end
	possible_letters << possible_letter
	puts
	puts
end

puts "Top 3 likely letters for key:"
for i in 0..(input - 1) do
	puts "Letter #{i + 1}: #{possible_letters[i].join(" ")}"
end

possible_keys = []
for i in 0..2 do
	for j in 0..2 do
		for k in 0..2 do
			for y in 0..2 do
				for z in 0..2 do
					possible_keys << possible_letters[0][i] + possible_letters[1][j] + possible_letters[2][k] + possible_letters[3][y] + possible_letters[4][z]
				end
			end
		end
	end
end


puts "Possible keys:"
puts possible_keys.join(" ")

puts
puts "English keys:"
puts possible_keys.select{|key| dictionary.key?(key)}.join(" ")

trying = true
while (trying) do
	print "Try key:"
	input = gets.strip
	key_index = 0
	result = ""
	text.chars.each do |c|
		result << (((alphabet.find_index(c) - alphabet.find_index(input[key_index]) + 26) % 26) + 'A'.ord).chr
		key_index += 1
		if key_index >= input.length()
			key_index = 0
		end
	end
	puts result
end
