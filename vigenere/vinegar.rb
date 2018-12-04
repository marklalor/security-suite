$alphabet = ('A'..'Z').to_a

$real_frequencies = {
	'E': 0.1202,
	'T': 0.0910,
	'A': 0.0812,
	'O': 0.0768,
	'I': 0.0731,
	'N': 0.0695,
	'S': 0.0628,
	'R': 0.0602,
	'H': 0.0592,
	'D': 0.0432,
	'L': 0.0398,
	'U': 0.0288,
	'C': 0.0271,
	'M': 0.0261,
	'F': 0.0230,
	'Y': 0.0211,
	'W': 0.0209,
	'G': 0.0203,
	'P': 0.0182,
	'B': 0.0149,
	'V': 0.0111,
	'K': 0.0069,
	'X': 0.0017,
	'Q': 0.0011,
	'J': 0.0010,
	'Z': 0.0007
}

def decypher(text, key)
	key_index = 0
	result = ""
	text.chars.each do |c|
		result << ((($alphabet.find_index(c) - $alphabet.find_index(key[key_index]) + 26) % 26) + 'A'.ord).chr
		key_index += 1
		if key_index >= key.length()
			key_index = 0
		end
	end
	return result
end

def find_frequency(text)
	counts = {}
	text.chars.each do |c|
		if counts.key?(c)
			counts[c] = counts[c] + 1
		else
			counts[c] = 1
		end
	end
	frequency = {}
	counts.each do |key, value|
		frequency[key] = counts[key].to_f / text.length()
	end
	return frequency
end

def frequency_correlation(text_frequency)
	sum = 0.0
	text_frequency.each do |key, freq|
		sum += (freq * $real_frequencies[key.to_sym])
	end
	return sum
end

cipher_text = "tx iof elq prdx at gtqqg, ve amg gsi icedx at gtqqg, ve amg gsi mur zj iwfosy, wg hee hup ess bq jacytwtbrdw, uh jlw fvr ptaqu zj nsytir, wg hee hup ibcps sr wanvqrhwmfm, ve amg gsi esndsz cs wmsvg, tx iof elq grlwab bq hmfxyieg, ve amg gsi edetrs cs ssbs, ve amg gsi iwaeid cs oiedntv, is ulh qjrccfvvyk nsszvq if, hi toq ysfvvyk nsszvq if, hi isep exz tzmzu qtvqqg es tsngiz, kr hids nwp scvyk pwepgf hup sfvrc amm – vy wtcee, xts cpvucq hee gb qed zvvi fvr avqgryx bsetsp, hulx eczp sr wgd rawftieh nfxtcetxusf trewfeip ca txe prtrs frniujro, jaf tzsp ce qsd sitp, ub gsi eicpvxogtzq rrrvqs bq gaaclvugby szzl."
puts cipher_text
gets

text = cipher_text.upcase.delete("^a-zA-Z")
puts text
gets

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
input = gets.strip
while (input.empty?)
	print "Period guess: "
	input = gets.strip
end
input = input.to_i
chunks = []
i = 0
while i < text.length do
	chunks << text[i, input]
	i += input
end

puts
puts chunks.join(" ")
gets

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
		possible_letter << $alphabet[($alphabet.find_index(letter_count[i].first) + 22) % 26]
	end
	possible_letters << possible_letter
	puts
end

gets

puts "Top 3 likely letters for key:"
for i in 0..(input - 1) do
	puts "Letter #{i + 1}: #{possible_letters[i].join(" ")}"
end

gets

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

gets

correlations = {}
possible_keys.each do |key|
	correlations[key] = frequency_correlation(find_frequency(decypher(text, key)))
end
correlations = correlations.sort_by{ |letter, count| count}.reverse

puts "Keys with highest English letter correlation frequencies:"
correlations.first(10).each do |corr|
	puts "Key: #{corr[0]}, correlation frequency: #{corr[1]}"
end

trying = true
puts
print "Try key (enter 'EXIT' to stop):"
input = gets.strip
trying = false if input.eql?("EXIT")
while (trying) do
	decyphered = decypher(text, input)
	puts decyphered
	puts
	print "Try key (enter 'EXIT' to stop):"
	input = gets.strip
	trying = false if input.eql?("EXIT")
end
