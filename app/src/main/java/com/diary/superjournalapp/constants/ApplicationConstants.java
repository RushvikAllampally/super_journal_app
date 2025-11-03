package com.diary.superjournalapp.constants;

import com.diary.superjournalapp.dto.QuoteDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConstants {

    public static final String CONTACT_EMAIL = "yourdiaryverse@gmail.com";

    public static final String MY_APP_NAME = "superJournalApp";
    public static final String FIRST_TIME_USER = "firstTimeUser";
    public static final String GRATITUDE_JOURNAL = "Gratitude Journal";
    public static final String REFLECTIVE_JOURNAL = "My Diary";
    public static final String BULLET_JOURNAL = "Bullet Journal";
    public static final String DREAM_JOURNAL = "Dream Journal";
    public static final String JOURNAL_ID_INTENT = "journalId";

    //shared preferences
    public static final String APP_USER_NAME = "appUserName";
    public static final String IS_PASSCODE_ENABLED = "isPasscodeEnabled";
    public static final String STREAK_PREF_KEY = "streak_counter";
    public static final String LAST_ENTRY_DATE_PREF_KEY = "last_entry_date";


    //intent
    public static final String IS_NEW_PASSCODE = "setNewPasscode";

    //notificationType
    public static final String NOTIFICATION_TYPE = "notificationType";
    public static final String Gratitude_NOTIFICATION_TYPE = "gratitude";
    public static final String DREAM_NOTIFICATION_TYPE = "dream";
    public static final String REFLECTIVE_NOTIFICATION_TYPE = "diary";
    public static final String BULLET_NOTIFICATION_TYPE = "bullet";
    public static final String AFFIRMATION_NOTIFICATION_TYPE = "affirmation";
    public static final String QUOTE_NOTIFICATION_TYPE = "quote";
    public static final String MOOD_NOTIFICATION_TYPE = "mood";
    public static final String DEFAULT_REMINDERS_NEEDED = "default_reminder_needed";

    //

    public static String[] AFFIRMATIONS =
            {
                    "I am capable of achieving my goals.",
                    "I believe in my abilities.",
                    "I am in control of my thoughts and emotions.",
                    "I am confident and self-assured.",
                    "I am worthy of love and respect.",
                    "I am resilient and can overcome any challenge.",
                    "I am grateful for all the good in my life.",
                    "I radiate positivity and attract positivity in return.",
                    "I am at peace with my past and excited for my future.",
                    "I am a constant work in progress, and that's okay.",
                    "I am open to new opportunities and experiences.",
                    "I am a magnet for success and good fortune.",
                    "I am in charge of my own happiness.",
                    "I am kind and compassionate to myself and others.",
                    "I am healthy, strong, and full of energy.",
                    "I am deserving of all the good things life has to offer.",
                    "I am free from worry and doubt.",
                    "I am in tune with my intuition and trust my inner wisdom.",
                    "I am constantly growing and evolving.",
                    "I am surrounded by love and support.",
                    "I am capable of handling whatever comes my way.",
                    "I am focused and determined in pursuit of my dreams.",
                    "I am a positive influence on those around me.",
                    "I am fearless and ready to take on challenges.",
                    "I am a source of inspiration to others.",
                    "I am prosperous and abundant in every way.",
                    "I am guided by my inner purpose and passion.",
                    "I am confident in my decisions and choices.",
                    "I am at peace with the present moment.",
                    "I am grateful for the lessons and blessings in my life.",
                    "I am free to be myself and express my true feelings.",
                    "I am aligned with my higher self and inner truth.",
                    "I am open to love and allow it to flow into my life.",
                    "I am worthy of success and achievements.",
                    "I am filled with courage and determination.",
                    "I am a beacon of positivity and light.",
                    "I am creating a life I love and deserve.",
                    "I am loved and cherished by those around me.",
                    "I am a master of my own destiny.",
                    "I am grateful for the abundance that surrounds me.",
                    "I am confident and comfortable in my own skin.",
                    "I am open to receiving all the good that life has to offer.",
                    "I am unstoppable and can overcome any obstacle.",
                    "I am a source of strength for myself and others.",
                    "I am at ease and in harmony with the universe.",
                    "I am a reflection of divine love and light.",
                    "I am focused on my goals and dreams.",
                    "I am free from negative thoughts and beliefs.",
                    "I am a magnet for success and prosperity.",
                    "I am deserving of happiness and fulfillment.",
                    "I am open to the infinite possibilities of the universe.",
                    "I am a powerful creator of my reality.",
                    "I am confident in my abilities and talents.",
                    "I am in control of my own life and choices.",
                    "I am grateful for the abundance that flows to me.",
                    "I am free from self-doubt and fear.",
                    "I am open to receiving love and positive energy.",
                    "I am worthy of all the good that comes my way.",
                    "I am a loving and compassionate person.",
                    "I am connected to the wisdom of the universe.",
                    "I am open to the beauty and joy of life.",
                    "I am confident in my uniqueness and individuality.",
                    "I am guided by my inner wisdom and intuition.",
                    "I am deserving of love and respect.",
                    "I am free to express my true self.",
                    "I am aligned with my inner purpose and passion.",
                    "I am grateful for the opportunities that come my way.",
                    "I am a magnet for happiness and positivity.",
                    "I am worthy of success and prosperity.",
                    "I am strong, confident, and capable.",
                    "I am in control of my thoughts and emotions.",
                    "I am open to new possibilities and experiences.",
                    "I am a constant learner and grow with each experience.",
                    "I am surrounded by love and support.",
                    "I am courageous and fearless.",
                    "I am a source of inspiration to others.",
                    "I am prosperous and abundant in all aspects of my life.",
                    "I am confident in my decisions and choices.",
                    "I am at peace with the present moment.",
                    "I am grateful for the lessons and blessings in my life.",
                    "I am free to express my true feelings and emotions.",
                    "I am aligned with my inner truth and authenticity.",
                    "I am open to giving and receiving love.",
                    "I am worthy of all the good things that come my way.",
                    "I am free from worry and doubt.",
                    "I am connected to the wisdom of the universe.",
                    "I am confident and comfortable with who I am.",
                    "I am open to receiving the abundance of life.",
                    "I am unstoppable and can overcome any challenge.",
                    "I am a source of strength and support to others.",
                    "I am in harmony with the flow of life.",
                    "I am a channel for divine love and light.",
                    "I am focused on my goals and dreams.",
                    "I am free from negative thoughts and beliefs.",
                    // Extended affirmation collection
                    "I trust the journey even when I don't understand it.",
                    "I am becoming the best version of myself.",
                    "Every day I am getting better and better.",
                    "I have the power to create the life I desire.",
                    "I am worthy of my dreams.",
                    "I choose to be happy right now.",
                    "I am enough just as I am.",
                    "My potential is unlimited.",
                    "I trust my intuition and inner wisdom.",
                    "I am proud of myself and my achievements.",
                    "I embrace change and welcome new opportunities.",
                    "I am mentally and physically strong.",
                    "I radiate confidence and self-assurance.",
                    "I am in perfect health and abundant wealth.",
                    "I choose to focus on what I can control.",
                    "I am learning valuable lessons from my challenges.",
                    "I attract positive energy and positive people.",
                    "I am calm, peaceful, and centered.",
                    "My life is filled with love, joy, and abundance.",
                    "I release all worry and embrace faith.",
                    "I am making a positive difference in the world.",
                    "I trust the timing of my life.",
                    "I am capable of amazing things.",
                    "My mind is clear and focused.",
                    "I choose progress over perfection.",
                    "I am patient with myself and my journey.",
                    "I celebrate every small victory.",
                    "I am deserving of rest and relaxation.",
                    "My voice matters and I speak my truth.",
                    "I am releasing all negative thoughts and emotions.",
                    "I attract abundance in all areas of my life.",
                    "I am brave enough to take risks.",
                    "I trust myself to make good decisions.",
                    "I am surrounded by beauty and inspiration.",
                    "I choose to see the good in every situation.",
                    "I am a powerful force for positive change.",
                    "My body is healthy and strong.",
                    "I am grateful for this moment.",
                    "I choose love over fear.",
                    "I am creating my own happiness.",
                    "I believe in my ability to succeed.",
                    "I am constantly expanding my comfort zone.",
                    "I am a unique and valuable person.",
                    "My past does not define my future.",
                    "I am open to receiving abundance.",
                    "I have everything I need within me.",
                    "I am worthy of all good things.",
                    "I choose to be optimistic and hopeful.",
                    "I am making wise and healthy choices.",
                    "I trust that everything is working out for my highest good.",
                    "I am confident in my unique gifts and talents.",
                    "I am resilient and bounce back from setbacks.",
                    "I choose joy and positivity in every moment.",
                    "I am financially secure and prosperous.",
                    "I honor my commitments to myself.",
                    "I am worthy of success and abundance.",
                    "I create opportunities wherever I go.",
                    "I am peaceful and at ease.",
                    "My possibilities are endless.",
                    "I choose to be kind to myself.",
                    "I am attracting my ideal life.",
                    "I have all the energy I need to accomplish my goals.",
                    "I am living my life with purpose and passion.",
                    "I release all self-doubt and believe in myself.",
                    "I am confident in my ability to learn and grow.",
                    "I choose to see obstacles as opportunities.",
                    "I am grateful for my body and treat it with respect.",
                    "I am creating the life of my dreams.",
                    "I trust in my abilities and express my true self.",
                    "I am worthy of happiness and fulfillment.",
                    "I choose thoughts that support my success.",
                    "I am deserving of peace and harmony.",
                    "I embrace my uniqueness and individuality.",
                    "I am making a meaningful contribution.",
                    "I choose to live in the present moment.",
                    "I am confident, capable, and strong.",
                    "I trust the process of life.",
                    "I am open to new ideas and perspectives.",
                    "I choose to focus on solutions, not problems.",
                    "I am worthy of love, respect, and kindness.",
                    "I am creating positive change in my life.",
                    "I have the courage to follow my heart.",
                    "I am grateful for all that I have.",
                    "I choose to be authentic and true to myself.",
                    "I am at peace with who I am.",
                    "I trust my path and walk it with joy.",
                    "I am deserving of all the blessings coming my way.",
                    "I choose to be present and mindful.",
                    "I am strong enough to face any challenge.",
                    "I believe in my dreams and my ability to achieve them.",
                    "I am transforming my life one day at a time.",
                    "I choose self-love and self-care.",
                    "I am worthy of beautiful and healthy relationships.",
                    "I trust my journey is unfolding perfectly.",
                    "I am grateful for the opportunity to grow.",
                    "I choose to let go of what no longer serves me.",
                    "I am creating a life that feels good on the inside.",
                    "I trust in divine timing.",
                    "I am surrounded by supportive and loving people.",
                    "I choose to be gentle with myself.",
                    "I am excited about the future.",
                    "I believe in the power of positive thinking.",
                    "I am worthy of taking up space.",
                    "I choose to honor my feelings and emotions.",
                    "I am continuously learning and evolving.",
                    "I trust my inner guidance.",
                    "I am proud of how far I've come.",
                    "I choose courage over comfort.",
                    "I am deserving of good health and vitality.",
                    "I trust that I am on the right path.",
                    "I am enough, I have enough, I do enough."};
    public static List<QuoteDto> QUOTES_ARRAY = new ArrayList<>();

    static {

        QUOTES_ARRAY.add(createQuote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"));
        QUOTES_ARRAY.add(createQuote("Success is not final, failure is not fatal: It is the courage to continue that counts.", "Winston Churchill"));
        QUOTES_ARRAY.add(createQuote("Your time is limited, don't waste it living someone else's life.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("Life is really simple, but we insist on making it complicated.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"));
        QUOTES_ARRAY.add(createQuote("In the end, it's not the years in your life that count. It's the life in your years.", "Abraham Lincoln"));
        QUOTES_ARRAY.add(createQuote("Don't count the days, make the days count.", "Muhammad Ali"));
        QUOTES_ARRAY.add(createQuote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"));
        QUOTES_ARRAY.add(createQuote("The best way to predict the future is to create it.", "Peter Drucker"));
        QUOTES_ARRAY.add(createQuote("Happiness is not something ready-made. It comes from your own actions.", "Dalai Lama"));
        QUOTES_ARRAY.add(createQuote("The only person you are destined to become is the person you decide to be.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The journey of a thousand miles begins with one step.", "Lao Tzu"));
        QUOTES_ARRAY.add(createQuote("Believe you can and you're halfway there.", "Theodore Roosevelt"));
        QUOTES_ARRAY.add(createQuote("Do not dwell in the past, do not dream of the future, concentrate the mind on the present moment.", "Buddha"));
        QUOTES_ARRAY.add(createQuote("Life is what happens when you're busy making other plans.", "John Lennon"));
        QUOTES_ARRAY.add(createQuote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"));
        QUOTES_ARRAY.add(createQuote("The only thing necessary for the triumph of evil is for good men to do nothing.", "Edmund Burke"));
        QUOTES_ARRAY.add(createQuote("You miss 100% of the shots you don't take.", "Wayne Gretzky"));
        QUOTES_ARRAY.add(createQuote("To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The only true wisdom is in knowing you know nothing.", "Socrates"));
        QUOTES_ARRAY.add(createQuote("The best revenge is massive success.", "Frank Sinatra"));
        QUOTES_ARRAY.add(createQuote("Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.", "Thomas A. Edison"));
        QUOTES_ARRAY.add(createQuote("If you want to achieve greatness stop asking for permission.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"));
        QUOTES_ARRAY.add(createQuote("In three words I can sum up everything I've learned about life: it goes on.", "Robert Frost"));
        QUOTES_ARRAY.add(createQuote("Change your thoughts and you change your world.", "Norman Vincent Peale"));
        QUOTES_ARRAY.add(createQuote("The biggest risk is not taking any risk. In a world that's changing quickly, the only strategy that is guaranteed to fail is not taking risks.", "Mark Zuckerberg"));
        QUOTES_ARRAY.add(createQuote("In the end, we will remember not the words of our enemies, but the silence of our friends.", "Martin Luther King Jr."));
        QUOTES_ARRAY.add(createQuote("The way to get started is to quit talking and begin doing.", "Walt Disney"));
        
        // Extended quote collection
        QUOTES_ARRAY.add(createQuote("The only impossible journey is the one you never begin.", "Tony Robbins"));
        QUOTES_ARRAY.add(createQuote("Everything you've ever wanted is on the other side of fear.", "George Addair"));
        QUOTES_ARRAY.add(createQuote("Dream big and dare to fail.", "Norman Vaughan"));
        QUOTES_ARRAY.add(createQuote("Courage is grace under pressure.", "Ernest Hemingway"));
        QUOTES_ARRAY.add(createQuote("Life shrinks or expands in proportion to one's courage.", "Anaïs Nin"));
        QUOTES_ARRAY.add(createQuote("What we fear doing most is usually what we most need to do.", "Tim Ferriss"));
        QUOTES_ARRAY.add(createQuote("The mind is everything. What you think you become.", "Buddha"));
        QUOTES_ARRAY.add(createQuote("Strive not to be a success, but rather to be of value.", "Albert Einstein"));
        QUOTES_ARRAY.add(createQuote("I have not failed. I've just found 10,000 ways that won't work.", "Thomas Edison"));
        QUOTES_ARRAY.add(createQuote("Twenty years from now you will be more disappointed by the things you didn't do.", "Mark Twain"));
        QUOTES_ARRAY.add(createQuote("It is during our darkest moments that we must focus to see the light.", "Aristotle"));
        QUOTES_ARRAY.add(createQuote("The best time to plant a tree was 20 years ago. The second best time is now.", "Chinese Proverb"));
        QUOTES_ARRAY.add(createQuote("An unexamined life is not worth living.", "Socrates"));
        QUOTES_ARRAY.add(createQuote("Eighty percent of success is showing up.", "Woody Allen"));
        QUOTES_ARRAY.add(createQuote("Your life does not get better by chance, it gets better by change.", "Jim Rohn"));
        QUOTES_ARRAY.add(createQuote("People who are crazy enough to think they can change the world, are the ones who do.", "Rob Siltanen"));
        QUOTES_ARRAY.add(createQuote("We must balance conspicuous consumption with conscious capitalism.", "Kevin Kruse"));
        QUOTES_ARRAY.add(createQuote("Everything has beauty, but not everyone can see.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("How wonderful it is that nobody need wait a single moment before starting to improve the world.", "Anne Frank"));
        QUOTES_ARRAY.add(createQuote("When I let go of what I am, I become what I might be.", "Lao Tzu"));
        QUOTES_ARRAY.add(createQuote("The secret of getting ahead is getting started.", "Mark Twain"));
        QUOTES_ARRAY.add(createQuote("It's not whether you get knocked down, it's whether you get up.", "Vince Lombardi"));
        QUOTES_ARRAY.add(createQuote("If you are working on something that you really care about, you don't have to be pushed.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("The most difficult thing is the decision to act, the rest is merely tenacity.", "Amelia Earhart"));
        QUOTES_ARRAY.add(createQuote("Fall seven times and stand up eight.", "Japanese Proverb"));
        QUOTES_ARRAY.add(createQuote("When everything seems to be going against you, remember that the airplane takes off against the wind.", "Henry Ford"));
        QUOTES_ARRAY.add(createQuote("We generate fears while we sit. We overcome them by action.", "Dr. Henry Link"));
        QUOTES_ARRAY.add(createQuote("Whether you think you can or you think you can't, you're right.", "Henry Ford"));
        QUOTES_ARRAY.add(createQuote("The only person you should try to be better than is the person you were yesterday.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("I didn't fail the test. I just found 100 ways to do it wrong.", "Benjamin Franklin"));
        QUOTES_ARRAY.add(createQuote("A person who never made a mistake never tried anything new.", "Albert Einstein"));
        QUOTES_ARRAY.add(createQuote("If you can dream it, you can do it.", "Walt Disney"));
        QUOTES_ARRAY.add(createQuote("The battles that count aren't the ones for gold medals.", "Jesse Owens"));
        QUOTES_ARRAY.add(createQuote("Do one thing every day that scares you.", "Eleanor Roosevelt"));
        QUOTES_ARRAY.add(createQuote("Good things come to people who wait, but better things come to those who go out and get them.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("If you do what you always did, you will get what you always got.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill"));
        QUOTES_ARRAY.add(createQuote("Just when the caterpillar thought the world was ending, he turned into a butterfly.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("The difference between ordinary and extraordinary is that little extra.", "Jimmy Johnson"));
        QUOTES_ARRAY.add(createQuote("Don't be afraid to give up the good to go for the great.", "John D. Rockefeller"));
        QUOTES_ARRAY.add(createQuote("I find that the harder I work, the more luck I seem to have.", "Thomas Jefferson"));
        QUOTES_ARRAY.add(createQuote("Success is the sum of small efforts repeated day in and day out.", "Robert Collier"));
        QUOTES_ARRAY.add(createQuote("If you want to lift yourself up, lift up someone else.", "Booker T. Washington"));
        QUOTES_ARRAY.add(createQuote("The real test is not whether you avoid this failure, but whether you let it harden or shame you.", "Oprah Winfrey"));
        QUOTES_ARRAY.add(createQuote("It is never too late to be what you might have been.", "George Eliot"));
        QUOTES_ARRAY.add(createQuote("You become what you believe.", "Oprah Winfrey"));
        QUOTES_ARRAY.add(createQuote("I would rather die of passion than of boredom.", "Vincent van Gogh"));
        QUOTES_ARRAY.add(createQuote("It is not what you do for your children, but what you have taught them to do for themselves.", "Ann Landers"));
        QUOTES_ARRAY.add(createQuote("The only way to have a friend is to be one.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("Build your own dreams, or someone else will hire you to build theirs.", "Farrah Gray"));
        QUOTES_ARRAY.add(createQuote("The pessimist sees difficulty in every opportunity. The optimist sees opportunity in every difficulty.", "Winston Churchill"));
        QUOTES_ARRAY.add(createQuote("Don't let yesterday take up too much of today.", "Will Rogers"));
        QUOTES_ARRAY.add(createQuote("You learn more from failure than from success. Don't let it stop you.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("It's not what happens to you, but how you react to it that matters.", "Epictetus"));
        QUOTES_ARRAY.add(createQuote("Make each day your masterpiece.", "John Wooden"));
        QUOTES_ARRAY.add(createQuote("Life is 10% what happens to you and 90% how you react to it.", "Charles R. Swindoll"));
        QUOTES_ARRAY.add(createQuote("Nothing is impossible, the word itself says 'I'm possible'!", "Audrey Hepburn"));
        QUOTES_ARRAY.add(createQuote("Keep your face always toward the sunshine and shadows will fall behind you.", "Walt Whitman"));
        QUOTES_ARRAY.add(createQuote("What lies behind us and what lies before us are tiny matters compared to what lies within us.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The question isn't who is going to let me; it's who is going to stop me.", "Ayn Rand"));
        QUOTES_ARRAY.add(createQuote("I alone cannot change the world, but I can cast a stone across the water to create many ripples.", "Mother Teresa"));
        QUOTES_ARRAY.add(createQuote("Remember no one can make you feel inferior without your consent.", "Eleanor Roosevelt"));
        QUOTES_ARRAY.add(createQuote("Life is not measured by the number of breaths we take, but by the moments that take our breath away.", "Maya Angelou"));
        QUOTES_ARRAY.add(createQuote("Happiness is not by chance, but by choice.", "Jim Rohn"));
        QUOTES_ARRAY.add(createQuote("If you want to go fast, go alone. If you want to go far, go together.", "African Proverb"));
        QUOTES_ARRAY.add(createQuote("The only limit to our realization of tomorrow is our doubts of today.", "Franklin D. Roosevelt"));
        QUOTES_ARRAY.add(createQuote("You are never too old to set another goal or to dream a new dream.", "C.S. Lewis"));
        QUOTES_ARRAY.add(createQuote("Try to be a rainbow in someone else's cloud.", "Maya Angelou"));
        QUOTES_ARRAY.add(createQuote("You are braver than you believe, stronger than you seem, and smarter than you think.", "A.A. Milne"));
        QUOTES_ARRAY.add(createQuote("Challenges are what make life interesting. Overcoming them is what makes life meaningful.", "Joshua J. Marine"));
        QUOTES_ARRAY.add(createQuote("If you can't explain it simply, you don't understand it well enough.", "Albert Einstein"));
        QUOTES_ARRAY.add(createQuote("Blessed are those who can give without remembering and take without forgetting.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("Do not go where the path may lead, go instead where there is no path and leave a trail.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("Be yourself; everyone else is already taken.", "Oscar Wilde"));
        QUOTES_ARRAY.add(createQuote("People often say that motivation doesn't last. Well, neither does bathing.", "Zig Ziglar"));
        QUOTES_ARRAY.add(createQuote("Life is what we make it, always has been, always will be.", "Grandma Moses"));
        QUOTES_ARRAY.add(createQuote("Dreaming, after all, is a form of planning.", "Gloria Steinem"));
        QUOTES_ARRAY.add(createQuote("Whatever the mind of man can conceive and believe, it can achieve.", "Napoleon Hill"));
        QUOTES_ARRAY.add(createQuote("First, have a definite, clear practical ideal; a goal, an objective.", "Aristotle"));
        QUOTES_ARRAY.add(createQuote("Either write something worth reading or do something worth writing.", "Benjamin Franklin"));
        QUOTES_ARRAY.add(createQuote("The only way to do great work is to love what you do.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("If you can dream it, you can achieve it.", "Zig Ziglar"));
        QUOTES_ARRAY.add(createQuote("Don't let what you cannot do interfere with what you can do.", "John Wooden"));
        QUOTES_ARRAY.add(createQuote("Dream as if you'll live forever, live as if you'll die today.", "James Dean"));
        QUOTES_ARRAY.add(createQuote("What you lack in talent can be made up with desire, hustle and giving 110% all the time.", "Don Zimmer"));
        QUOTES_ARRAY.add(createQuote("Do what you can with all you have, wherever you are.", "Theodore Roosevelt"));
        QUOTES_ARRAY.add(createQuote("You are never too old to set another goal or to dream a new dream.", "Les Brown"));
        QUOTES_ARRAY.add(createQuote("Life is short, and it is up to you to make it sweet.", "Sarah Louise Delany"));
        QUOTES_ARRAY.add(createQuote("The power of imagination makes us infinite.", "John Muir"));
        QUOTES_ARRAY.add(createQuote("The purpose of our lives is to be happy.", "Dalai Lama"));
        QUOTES_ARRAY.add(createQuote("May you live every day of your life.", "Jonathan Swift"));
        QUOTES_ARRAY.add(createQuote("Not how long, but how well you have lived is the main thing.", "Seneca"));
        QUOTES_ARRAY.add(createQuote("The whole secret of a successful life is to find out what is one's destiny to do.", "Henry Ford"));
        QUOTES_ARRAY.add(createQuote("In order to write about life first you must live it.", "Ernest Hemingway"));
        QUOTES_ARRAY.add(createQuote("Keep smiling, because life is a beautiful thing and there's so much to smile about.", "Marilyn Monroe"));
        QUOTES_ARRAY.add(createQuote("Life is a long lesson in humility.", "James M. Barrie"));
        QUOTES_ARRAY.add(createQuote("In three words I can sum up everything about life: it goes on.", "Robert Frost"));
        QUOTES_ARRAY.add(createQuote("Love the life you live. Live the life you love.", "Bob Marley"));
        QUOTES_ARRAY.add(createQuote("Life is either a daring adventure or nothing at all.", "Helen Keller"));
        QUOTES_ARRAY.add(createQuote("You have brains in your head. You have feet in your shoes.", "Dr. Seuss"));
        QUOTES_ARRAY.add(createQuote("Good friends, good books, and a sleepy conscience: this is the ideal life.", "Mark Twain"));
        QUOTES_ARRAY.add(createQuote("To live is the rarest thing in the world. Most people exist, that is all.", "Oscar Wilde"));
        QUOTES_ARRAY.add(createQuote("Live in the sunshine, swim the sea, drink the wild air.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The greatest glory in living lies not in never failing, but in rising every time we fail.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("You only live once, but if you do it right, once is enough.", "Mae West"));
        QUOTES_ARRAY.add(createQuote("You have power over your mind — not outside events. Realize this, and you will find strength.", "Marcus Aurelius"));
        QUOTES_ARRAY.add(createQuote("The impediment to action advances action. What stands in the way becomes the way.", "Marcus Aurelius"));
        QUOTES_ARRAY.add(createQuote("Waste no more time arguing what a good man should be. Be one.", "Marcus Aurelius"));
        QUOTES_ARRAY.add(createQuote("The happiness of your life depends upon the quality of your thoughts.", "Marcus Aurelius"));
        QUOTES_ARRAY.add(createQuote("We suffer more often in imagination than in reality.", "Seneca"));
        QUOTES_ARRAY.add(createQuote("Luck is what happens when preparation meets opportunity.", "Seneca"));
        QUOTES_ARRAY.add(createQuote("It is not the man who has too little, but the man who desires more, that is poor.", "Seneca"));
        QUOTES_ARRAY.add(createQuote("No man is free who is not master of himself.", "Epictetus"));
        QUOTES_ARRAY.add(createQuote("First say to yourself what you would be; and then do what you have to do.", "Epictetus"));
        QUOTES_ARRAY.add(createQuote("It does not matter how slowly you go as long as you do not stop.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("Life is really simple, but we insist on making it complicated.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("Go confidently in the direction of your dreams! Live the life you've imagined.", "Henry David Thoreau"));
        QUOTES_ARRAY.add(createQuote("In the end, it's not the years in your life that count. It's the life in your years.", "Abraham Lincoln"));
        QUOTES_ARRAY.add(createQuote("Do what you can, with what you have, where you are.", "Theodore Roosevelt"));
        QUOTES_ARRAY.add(createQuote("The only thing we have to fear is fear itself.", "Franklin D. Roosevelt"));
        QUOTES_ARRAY.add(createQuote("Remember no one can make you feel inferior without your consent.", "Eleanor Roosevelt"));
        QUOTES_ARRAY.add(createQuote("People will forget what you said, people will forget what you did, but people will never forget how you made them feel.", "Maya Angelou"));
        QUOTES_ARRAY.add(createQuote("You can't use up creativity. The more you use, the more you have.", "Maya Angelou"));
        QUOTES_ARRAY.add(createQuote("In the middle of every difficulty lies opportunity.", "Albert Einstein"));
        QUOTES_ARRAY.add(createQuote("He who has a why to live can bear almost any how.", "Friedrich Nietzsche"));
        QUOTES_ARRAY.add(createQuote("Not all of us can do great things. But we can do small things with great love.", "Mother Teresa"));
        QUOTES_ARRAY.add(createQuote("The soul becomes dyed with the color of its thoughts.", "Marcus Aurelius"));
        QUOTES_ARRAY.add(createQuote("When we are no longer able to change a situation, we are challenged to change ourselves.", "Viktor E. Frankl"));
        QUOTES_ARRAY.add(createQuote("Let everything happen to you: beauty and terror. Just keep going. No feeling is final.", "Rainer Maria Rilke"));
        QUOTES_ARRAY.add(createQuote("Until you make the unconscious conscious, it will direct your life and you will call it fate.", "Carl Jung"));
        QUOTES_ARRAY.add(createQuote("You do not rise to the level of your goals. You fall to the level of your systems.", "James Clear"));
        QUOTES_ARRAY.add(createQuote("Desire is a contract you make with yourself to be unhappy until you get what you want.", "Naval Ravikant"));
        QUOTES_ARRAY.add(createQuote("Tell me, what is it you plan to do with your one wild and precious life?", "Mary Oliver"));
        QUOTES_ARRAY.add(createQuote("If there's a book that you want to read, but it hasn't been written yet, then you must write it.", "Toni Morrison"));
        QUOTES_ARRAY.add(createQuote("It is good to have an end to journey toward; but it is the journey that matters, in the end.", "Ursula K. Le Guin"));
        QUOTES_ARRAY.add(createQuote("Muddy water is best cleared by leaving it alone.", "Alan Watts"));
        QUOTES_ARRAY.add(createQuote("Life can only be understood backwards; but it must be lived forwards.", "Soren Kierkegaard"));
        QUOTES_ARRAY.add(createQuote("In the depth of winter, I finally learned that within me there lay an invincible summer.", "Albert Camus"));
        QUOTES_ARRAY.add(createQuote("Everyone thinks of changing the world, but no one thinks of changing himself.", "Leo Tolstoy"));
        QUOTES_ARRAY.add(createQuote("Your pain is the breaking of the shell that encloses your understanding.", "Khalil Gibran"));
        QUOTES_ARRAY.add(createQuote("Nothing ever goes away until it has taught us what we need to know.", "Pema Chodron"));
        QUOTES_ARRAY.add(createQuote("Life shrinks or expands in proportion to one's courage.", "Anais Nin"));

    }

    public static QuoteDto createQuote(String quote, String author) {
        return new QuoteDto(quote, author);
    }


    //mood level messages
    public static List<String> VERY_HAPPY_MOOD_MSGS = new ArrayList<>();

    static {
        VERY_HAPPY_MOOD_MSGS.add("Great to hear! Wishing you continued joy !");
        VERY_HAPPY_MOOD_MSGS.add("Your happiness is contagious. Keep shining !");
        VERY_HAPPY_MOOD_MSGS.add("Thrilled for you! May the good vibes last");
        VERY_HAPPY_MOOD_MSGS.add("Fantastic! Keep the positivity flowing");
        VERY_HAPPY_MOOD_MSGS.add("Your joy is a beacon of light. Enjoy the moment !");
    }

    public static List<String> HAPPY_MOOD_MSGS = new ArrayList<>();

    static {
        HAPPY_MOOD_MSGS.add("Choosing happiness is a win! Keep smiling");
        HAPPY_MOOD_MSGS.add("Happiness suits you well! Stay cheerful");
        HAPPY_MOOD_MSGS.add("Your positive energy is awesome !");
        HAPPY_MOOD_MSGS.add("Happy vibes all around. Keep it up !");
        HAPPY_MOOD_MSGS.add("Cheers to your happiness today !");
    }


    public static List<String> NEUTRAL_MOOD_MSGS = new ArrayList<>();

    static {
        NEUTRAL_MOOD_MSGS.add("A neutral day is full of possibilities. Seize them!");
        NEUTRAL_MOOD_MSGS.add("In the middle ground, where surprises happen. Enjoy!");
        NEUTRAL_MOOD_MSGS.add("Every day has its unique charm. Find yours today.");
        NEUTRAL_MOOD_MSGS.add("Embracing the calm. Here's to a peaceful day!");
        NEUTRAL_MOOD_MSGS.add("Neutral is the canvas. Paint it with small joys.");
    }

    public static List<String> SAD_MOOD_MSGS = new ArrayList<>();

    static {
        SAD_MOOD_MSGS.add("Sending warmth during tough times. You're not alone.");
        SAD_MOOD_MSGS.add("It's okay to feel sad. Take your time, breathe.");
        SAD_MOOD_MSGS.add("Sorrow comes and goes. Tomorrow is a new day.");
        SAD_MOOD_MSGS.add("Rainy days make the flowers bloom. Stay strong.");
        SAD_MOOD_MSGS.add("Your feelings are valid. Reach out if you need to talk.");
    }

    public static List<String> VERY_SAD_MOOD_MSGS = new ArrayList<>();

    static {
        VERY_SAD_MOOD_MSGS.add("During tough times, you're not alone. Reach out.");
        VERY_SAD_MOOD_MSGS.add("Sending strength during challenging moments.");
        VERY_SAD_MOOD_MSGS.add("Sadness may linger, but it doesn't define you.");
        VERY_SAD_MOOD_MSGS.add("Dark days pass. Hold on; brighter ones await.");
        VERY_SAD_MOOD_MSGS.add("You're stronger than you know. Take one step at a time.");
    }


    public static Map<String,Integer> NOTIFICATION_TYPE_REQUEST_CODE = new HashMap<>();

    static {
        NOTIFICATION_TYPE_REQUEST_CODE.put("diary",1);
        NOTIFICATION_TYPE_REQUEST_CODE.put("bullet",2);
        NOTIFICATION_TYPE_REQUEST_CODE.put("dream",3);
        NOTIFICATION_TYPE_REQUEST_CODE.put("quote",4);
        NOTIFICATION_TYPE_REQUEST_CODE.put("affirmation",5);
        NOTIFICATION_TYPE_REQUEST_CODE.put("gratitude",6);
        NOTIFICATION_TYPE_REQUEST_CODE.put("mood",7);
    }

    public static List<String> GRATITUDE_PROMPTS = new ArrayList<>();

    static {
        //list of 21 prompts
        GRATITUDE_PROMPTS.add("What made you smile today?");
        GRATITUDE_PROMPTS.add("Name three things that brought you joy.");
        GRATITUDE_PROMPTS.add("What achievements are you proud of today?");
        GRATITUDE_PROMPTS.add("Describe a person you are thankful to have in your life.");
        GRATITUDE_PROMPTS.add("Reflect on a recent act of kindness you experienced.");
        GRATITUDE_PROMPTS.add("List three things in nature you are grateful for.");
        GRATITUDE_PROMPTS.add("Write about a skill or talent you are thankful for having.");
        GRATITUDE_PROMPTS.add("Share a positive experience from today.");
        GRATITUDE_PROMPTS.add("Reflect on a challenge you faced and the lessons learned.");
        GRATITUDE_PROMPTS.add("Write about a place that brings you peace and gratitude.");
        GRATITUDE_PROMPTS.add("List three small pleasures you enjoyed today.");
        GRATITUDE_PROMPTS.add("Reflect on the support you received from others.");
        GRATITUDE_PROMPTS.add("Write about a moment of personal growth.");
        GRATITUDE_PROMPTS.add("Express gratitude for your health and well-being.");
        GRATITUDE_PROMPTS.add("What made today different from other days?");
        GRATITUDE_PROMPTS.add("Write about a positive change you've noticed recently.");
        GRATITUDE_PROMPTS.add("Reflect on something you learned today.");
        GRATITUDE_PROMPTS.add("List three things you love about yourself.");
        GRATITUDE_PROMPTS.add("Write about a happy memory that brings you joy.");
        GRATITUDE_PROMPTS.add("List things that you are grateful for today?");
        GRATITUDE_PROMPTS.add("Express gratitude for the opportunities in your life.");
    }

    public static List<String> REFLECTIVE_PROMPTS = new ArrayList<>();

    static {
        // Reflective journal prompts
        REFLECTIVE_PROMPTS.add("How are you feeling today and why?");
        REFLECTIVE_PROMPTS.add("What was the highlight of your day?");
        REFLECTIVE_PROMPTS.add("What challenged you today and how did you handle it?");
        REFLECTIVE_PROMPTS.add("Describe a conversation that impacted you recently.");
        REFLECTIVE_PROMPTS.add("What is something you're currently worried about?");
        REFLECTIVE_PROMPTS.add("What is something you're looking forward to?");
        REFLECTIVE_PROMPTS.add("Reflect on a mistake you made and what you learned.");
        REFLECTIVE_PROMPTS.add("What boundaries do you need to set or maintain?");
        REFLECTIVE_PROMPTS.add("How have you practiced self-care recently?");
        REFLECTIVE_PROMPTS.add("What are your current short-term and long-term goals?");
        REFLECTIVE_PROMPTS.add("Write a letter to your future self.");
        REFLECTIVE_PROMPTS.add("What's something you need to forgive yourself for?");
        REFLECTIVE_PROMPTS.add("How have you grown in the past year?");
        REFLECTIVE_PROMPTS.add("What's a recent situation you would handle differently now?");
        REFLECTIVE_PROMPTS.add("Write about a personal strength you've discovered.");
        REFLECTIVE_PROMPTS.add("Describe your ideal day from start to finish.");
        REFLECTIVE_PROMPTS.add("What relationships are currently nurturing you?");
        REFLECTIVE_PROMPTS.add("What relationships are currently draining you?");
        REFLECTIVE_PROMPTS.add("What does success mean to you right now?");
        REFLECTIVE_PROMPTS.add("If you could change one thing about your life, what would it be?");
    }
    
    public static List<String> DREAM_PROMPTS = new ArrayList<>();

    static {
        // Dream journal prompts
        DREAM_PROMPTS.add("Describe your dream in as much detail as you can remember.");
        DREAM_PROMPTS.add("What emotions did you feel during your dream?");
        DREAM_PROMPTS.add("Were there any recurring symbols or themes in your dream?");
        DREAM_PROMPTS.add("Did any people from your life appear in your dream?");
        DREAM_PROMPTS.add("Was there anything unusual or impossible in your dream?");
        DREAM_PROMPTS.add("Did your dream seem to relate to anything happening in your waking life?");
        DREAM_PROMPTS.add("Did you have any realizations during your dream?");
        DREAM_PROMPTS.add("What would you change about the dream if you could?");
        DREAM_PROMPTS.add("Was your dream in color or black and white?");
        DREAM_PROMPTS.add("Did you have any physical sensations in your dream?");
        DREAM_PROMPTS.add("How did the setting of your dream make you feel?");
        DREAM_PROMPTS.add("What might your subconscious be trying to tell you?");
        DREAM_PROMPTS.add("Did you have any control over what happened in your dream?");
        DREAM_PROMPTS.add("Did your dream remind you of any past experiences?");
        DREAM_PROMPTS.add("How did you feel when you woke up from your dream?");
    }

    public static List<String> BULLET_PROMPTS = new ArrayList<>();

    static {
        // Bullet journal prompts
        BULLET_PROMPTS.add("Three important tasks to complete today.");
        BULLET_PROMPTS.add("Weekly goals to accomplish.");
        BULLET_PROMPTS.add("Things I need to buy soon.");
        BULLET_PROMPTS.add("Books I want to read.");
        BULLET_PROMPTS.add("Project milestones to track.");
        BULLET_PROMPTS.add("Ideas I want to explore further.");
        BULLET_PROMPTS.add("People I need to contact.");
        BULLET_PROMPTS.add("Skills I want to develop.");
        BULLET_PROMPTS.add("Places I want to visit.");
        BULLET_PROMPTS.add("Self-care activities to schedule.");
        BULLET_PROMPTS.add("Habits I want to build.");
        BULLET_PROMPTS.add("Monthly goals to achieve.");
        BULLET_PROMPTS.add("Upcoming events to prepare for.");
        BULLET_PROMPTS.add("Meal planning for the week.");
        BULLET_PROMPTS.add("Budget and expense tracking items.");
    }

    // Daily prompts constant
    public static final String DAILY_PROMPTS_KEY = "dailyPromptsEnabled";


}
